package com.mercateo.rest.hateoas.client.impl.sse;

import java.net.URI;
import java.util.Optional;

import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.InboundEvent;

import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.SSEObserver;
import com.mercateo.rest.hateoas.client.impl.ResponseBuilder;

import lombok.NonNull;

public class SSEListener<T> implements EventListener {

    private Class<T> clazz;

    private SSEObserver<T> sseObserver;

    private ResponseBuilder responseBuilder;

    private String mainEventName;

    private URI uri;

    public SSEListener(@NonNull Class<T> clazz, @NonNull ResponseBuilder responseBuilder,
            @NonNull SSEObserver<T> sseObserver, @NonNull String mainEventName, URI uri) {
        this.mainEventName = mainEventName;
        this.clazz = clazz;
        this.responseBuilder = responseBuilder;
        this.sseObserver = sseObserver;
        this.uri = uri;
    }

    @Override
    public void onEvent(InboundEvent inboundEvent) {
        String eventName = inboundEvent.getName();
        if (mainEventName.equals(eventName)) {
            Optional<Response<T>> resp;
            try {
                resp = responseBuilder.buildResponse(inboundEvent.readData(), clazz, uri);
            } catch (Exception e) {
                sseObserver.onError(new ParsingFailedException(e, inboundEvent.getId()));
                return;
            }
            sseObserver.onEvent(resp.get());
        } else {
            sseObserver.onSignal(eventName);
        }

    }

    public void onError(String errorName) {
        sseObserver.onError(errorName);
    }

}
