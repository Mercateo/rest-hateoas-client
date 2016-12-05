package com.mercateo.rest.hateoas.client.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.JerseyInvocation;
import org.glassfish.jersey.uri.UriTemplate;
import org.reflections.ReflectionUtils;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.common.rest.schemagen.link.LinkCreator;
import com.mercateo.common.rest.schemagen.link.relation.Relation;
import com.mercateo.rest.hateoas.client.ListResponse;
import com.mercateo.rest.hateoas.client.OngoingResponse;
import com.mercateo.rest.hateoas.client.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OngoingResponseImpl<S> implements OngoingResponse<S> {
	@Wither
	private Object requestObject;

	@NonNull
	private Class<S> responseClass;

	@NonNull
	private JsonHyperSchema jsonHyperSchema;

	@NonNull
	private ResponseBuilder responseBuilder;

	@Override
	public Optional<Response<S>> callWithRel(@NonNull String rel) {

		String responseString = getResponse(rel);
		if (responseString == null) {
			return Optional.empty();
		}

		return responseBuilder.buildResponse(responseString, responseClass);

	}

	@Override
	public Optional<ListResponse<S>> callListWithRel(@NonNull String rel) {
		String responseString = getResponse(rel);
		if (responseString == null) {
			return Optional.empty();
		}
		return responseBuilder.buildListResponse(responseString, responseClass);
	}

	private String getResponse(String rel) {
		Optional<Link> linkOption = jsonHyperSchema.getByRel(() -> Relation.of(rel));
		if (!linkOption.isPresent()) {
			return null;
		}
		Link link = linkOption.get();

		String method = link.getParams().get(LinkCreator.METHOD_PARAM_KEY);
		URI uri = resolveTemplateParams(link, method);

		WebTarget target = responseBuilder.getClient().target(uri);

		Builder b = target.request(MediaType.APPLICATION_JSON_TYPE);
		javax.ws.rs.core.Response response;
		if (requestObject != null && bodyIsAllowed(method)) {
			response = b.method(method, Entity.json(requestObject));
		} else {
			response = b.method(method);
		}
		if (response.getStatus() >= 300) {
			throw new WebApplicationException(response);
		}
		String responseString = response.readEntity(String.class);
		return responseString;
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

	private URI resolveTemplateParams(Link link, String method) {
		if (method.equalsIgnoreCase("get") && requestObject != null) {
			String uriString = link.getUri().toASCIIString();
			try {
				uriString = URLDecoder.decode(uriString, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new ProcessingException(e);
			}
			UriTemplate uriTemplate = new UriTemplate(uriString);
			UriBuilder uriBuilder = UriBuilder.fromUri(uriString);
			List<String> vars = uriTemplate.getTemplateVariables();
			if (vars.size() > 0) {
				for (String var : vars) {
					@SuppressWarnings("unchecked")
					Set<Field> matchingFields = ReflectionUtils.getAllFields(requestObject.getClass(),
							f -> f.getName().equals(var));
					if(matchingFields.isEmpty()) {
						throw new IllegalStateException(
								"No field found for the template variable " + var);
					} else if (matchingFields.size() != 1) {
						throw new IllegalStateException(
								"There is more than one field for the template variable " + var + ": " + matchingFields);
					}
					Field matchingField = matchingFields.iterator().next();
					matchingField.setAccessible(true);
					try {
						uriBuilder.resolveTemplate(var, matchingField.get(requestObject));
					} catch (IllegalAccessException e) {
						throw new ProcessingException(" Should never happen :-)");
					}
				}
			}
			return uriBuilder.build();
		}
		return link.getUri();

	}

}
