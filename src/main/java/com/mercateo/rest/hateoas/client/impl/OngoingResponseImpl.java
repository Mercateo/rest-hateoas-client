package com.mercateo.rest.hateoas.client.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.JerseyInvocation;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.uri.UriTemplate;
import org.reflections.ReflectionUtils;

import com.google.common.annotations.VisibleForTesting;
import com.mercateo.rest.hateoas.client.ListResponse;
import com.mercateo.rest.hateoas.client.OngoingResponse;
import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.SSEObserver;
import com.mercateo.rest.hateoas.client.impl.sse.EventSourceWithCloseGuard;
import com.mercateo.rest.hateoas.client.impl.sse.SSEListener;
import com.mercateo.rest.hateoas.client.schema.ClientHyperSchema;
import com.mercateo.rest.hateoas.client.schema.SchemaLink;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class OngoingResponseImpl<S> implements OngoingResponse<S> {
    private static Object lock = new Object();

    @Value
    private static class Pair {
        WebTarget target;

        String method;
    }

    @VisibleForTesting
    static final String METHOD_PARAM_KEY = "method";

    @Wither
    private Object requestObject;

    @NonNull
    private Class<S> responseClass;

    @NonNull
    private ClientHyperSchema jsonHyperSchema;

    @NonNull
    private ResponseBuilder responseBuilder;

    @NonNull
    private URI uri;

    @Override
    public Optional<Response<S>> callWithRel(@NonNull String rel) {

        String responseString = getResponse(rel);
        if (responseString == null) {
            return Optional.empty();
        }

        return responseBuilder.buildResponse(responseString, responseClass, uri);

    }

    @Override
    public Optional<ListResponse<S>> callListWithRel(@NonNull String rel) {
        String responseString = getResponse(rel);
        if (responseString == null) {
            return Optional.empty();
        }
        return responseBuilder.buildListResponse(responseString, responseClass, uri);
    }

    private String getResponse(String rel) {
        Pair pair = resolve(rel);
        if (pair == null) {
            return null;
        }
        Builder b = pair.target.request(MediaType.APPLICATION_JSON_TYPE);
        javax.ws.rs.core.Response response;
        if (requestObject != null && bodyIsAllowed(pair.method)) {
            response = b.method(pair.method, Entity.json(requestObject));
        } else {
            response = b.method(pair.method);
        }
        if (response.getStatus() >= 300) {
            throw new WebApplicationException(response);
        }
        String responseString = response.readEntity(String.class);
        return responseString;
    }

    private Pair resolve(String rel) {
        Optional<SchemaLink> linkOption = jsonHyperSchema.getByRel(rel);
        if (!linkOption.isPresent()) {
            return null;
        }
        SchemaLink link = linkOption.get();

        String method = link.getMap().get(METHOD_PARAM_KEY);
        if (method == null) {
            method = "GET";
        }

        URI resolvedUri = uri.resolve(resolveTemplateParams(link, method));
        log.debug("resolving to " + resolvedUri);

        WebTarget target = responseBuilder.getClient().target(resolvedUri);

        target = resolveQueryParams(target, link, method);
        return new Pair(target, method);
    }

    /**
     * @see JerseyInvocation#initializeMap()
     * 
     * @param method
     * @return true if a request body is allowed for the given method
     */
    private static boolean bodyIsAllowed(String method) {
        return method.equalsIgnoreCase("put") || method.equalsIgnoreCase("post");
    }

    private URI resolveTemplateParams(SchemaLink link, String method) {
        Map<String, String> pathParams = new HashMap<>();
        UriTemplate uriTemplate = link.getHref();
        if (method.equalsIgnoreCase("get") && requestObject != null) {
            List<String> vars = uriTemplate.getTemplateVariables();
            if (vars.size() > 0) {

                for (String var : vars) {
                    @SuppressWarnings("unchecked")
                    Set<Field> matchingFields = ReflectionUtils.getAllFields(requestObject
                            .getClass(), f -> f.getName().equals(var));
                    if (matchingFields.isEmpty()) {
                        throw new IllegalStateException("No field found for the template variable "
                                + var);
                    } else if (matchingFields.size() != 1) {
                        throw new IllegalStateException(
                                "There is more than one field for the template variable " + var
                                        + ": " + matchingFields);
                    }
                    Field matchingField = matchingFields.iterator().next();
                    matchingField.setAccessible(true);
                    try {
                        pathParams.put(var, matchingField.get(requestObject).toString());
                    } catch (IllegalAccessException e) {
                        throw new ProcessingException(" Should never happen :-)");
                    }
                }
            }
        }
        try {
            return new URI(uriTemplate.createURI(pathParams));
        } catch (URISyntaxException e) {
            throw new ProcessingException(e);
        }

    }

    @Override
    public Optional<AutoCloseable> subscribe(@NonNull String rel, @NonNull SSEObserver<S> observer,
            @NonNull String mainEventName, long reconnectionTime) {
        checkArgument(reconnectionTime > 0);
        Pair pair = resolve(rel);
        if (pair == null) {
            return Optional.empty();
        }
        // this hack is needed because of
        // https://github.com/jersey/jersey/pull/3600
        // setting a global last event id header via LastEventIdHeaderFilter and
        // synchronizing all sse
        // sources :-(
        synchronized (lock) {


            // this hack is needed because of
            // https://github.com/jersey/jersey/pull/3600
        	if (observer.lastKnownEventId().isPresent()) {
                pair.target.property(SseFeature.LAST_EVENT_ID_HEADER, observer.lastKnownEventId()
                        .get());
            }
            EventSource eventSource = EventSource.target(pair.target).named("SSE" + UUID
                    .randomUUID()).usePersistentConnections().reconnectingEvery(reconnectionTime,
                            TimeUnit.MILLISECONDS).build();
            SSEListener<S> sseListener = new SSEListener<>(responseClass, responseBuilder, observer,
                    mainEventName, uri);
            eventSource.register(sseListener);
            EventSourceWithCloseGuard ev = new EventSourceWithCloseGuard(eventSource,
                    reconnectionTime, sseListener);
            ev.open();

            return Optional.of(ev);
        }
    }

    @SneakyThrows
    private WebTarget resolveQueryParams(WebTarget target, SchemaLink link, String method) {
        UriTemplate uriTemplate = link.getHref();
        if (method.equalsIgnoreCase("get") && requestObject != null) {
            List<String> vars = uriTemplate.getTemplateVariables();
            Set<Field> matchingFields = ReflectionUtils.getAllFields(requestObject.getClass(),
                    f -> !vars.contains(f.getName()));
            for (Field field : matchingFields) {
                field.setAccessible(true);
                if (field.get(requestObject) != null) {
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        Collection<?> value = (Collection<?>) field.get(requestObject);
                        String[] values = value.stream().map(Object::toString).collect(Collectors
                                .toList()).toArray(new String[0]);
                        target = target.queryParam(field.getName(), values);
                    } else {
                        target = target.queryParam(field.getName(), field.get(requestObject)
                                .toString());
                    }
                }
            }
        }
        return target;
    }
}
