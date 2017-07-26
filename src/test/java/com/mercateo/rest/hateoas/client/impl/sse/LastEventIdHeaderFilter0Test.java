package com.mercateo.rest.hateoas.client.impl.sse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.media.sse.SseFeature;
import org.junit.Test;

public class LastEventIdHeaderFilter0Test {

    private LastEventIdHeaderFilter uut = new LastEventIdHeaderFilter();

    @Test
    public void testFilterWithnoHeader() throws IOException {
        ClientRequestContext requestContext = mock(ClientRequestContext.class);
        Configuration config = mock(Configuration.class);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

        when(requestContext.getConfiguration()).thenReturn(config);
        when(requestContext.getHeaders()).thenReturn(headers);
        when(config.getProperty(SseFeature.LAST_EVENT_ID_HEADER)).thenReturn("1");
        uut.filter(requestContext);
        assertEquals("1", headers.getFirst(SseFeature.LAST_EVENT_ID_HEADER));
    }

    @Test
    public void testFilterWithHeaderAlreadySet() throws IOException {
        ClientRequestContext requestContext = mock(ClientRequestContext.class);
        Configuration config = mock(Configuration.class);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

        when(requestContext.getConfiguration()).thenReturn(config);
        when(requestContext.getHeaders()).thenReturn(headers);
        when(config.getProperty(SseFeature.LAST_EVENT_ID_HEADER)).thenReturn("1");
        when(requestContext.getHeaderString(SseFeature.LAST_EVENT_ID_HEADER)).thenReturn("2");
        uut.filter(requestContext);
        assertTrue(headers.isEmpty());
    }

    @Test
    public void testFilterWithnoHeaderAnsNoPRoperty() throws IOException {
        ClientRequestContext requestContext = mock(ClientRequestContext.class);
        Configuration config = mock(Configuration.class);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

        when(requestContext.getConfiguration()).thenReturn(config);
        when(requestContext.getHeaders()).thenReturn(headers);

        uut.filter(requestContext);
        assertTrue(headers.isEmpty());
    }

}
