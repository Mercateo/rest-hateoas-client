package com.mercateo.rest.hateoas.client.impl.sse;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.glassfish.jersey.media.sse.SseFeature;

@Priority(Priorities.HEADER_DECORATOR)
public class LastEventIdHeaderFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (requestContext.getHeaderString(SseFeature.LAST_EVENT_ID_HEADER) == null) {
            String lastKnownEventId = (String) requestContext.getConfiguration().getProperty(
                    SseFeature.LAST_EVENT_ID_HEADER);
            if (lastKnownEventId != null) {
                requestContext.getHeaders().putSingle(SseFeature.LAST_EVENT_ID_HEADER,
                        lastKnownEventId);
            }
        }

    }

}
