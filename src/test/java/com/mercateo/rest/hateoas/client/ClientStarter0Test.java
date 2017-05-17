package com.mercateo.rest.hateoas.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyInvocation.Builder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.media.sse.SseFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientStarter0Test {
    @Mock
    private JerseyClientBuilder jerseyClientBuilder;

    @Mock
    private JerseyClient client;

    @Mock
    private JerseyWebTarget webTarget;

    @InjectMocks
    private ClientStarter uut;

    @Mock
    private Builder builder;

    @Mock
    private javax.ws.rs.core.Response resonse;

    @Test
    public void testCreate() throws Exception {
        when(jerseyClientBuilder.build()).thenReturn(client);
        when(jerseyClientBuilder.register(SseFeature.class)).thenReturn(jerseyClientBuilder);
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.request(any(MediaType.class))).thenReturn(builder);
        when(builder.get()).thenReturn(resonse);
        when(resonse.readEntity(String.class)).thenReturn("");

        Response<Object> response = uut.create("http://mercateo.com/test", Object.class);
        verify(jerseyClientBuilder).build();
        verify(client).target(anyString());
        verify(webTarget).request(any(MediaType.class));
        verify(builder).get();
        verify(resonse).readEntity(String.class);
    }

    @Test
    public void testCreateWithConfig() throws Exception {
        when(jerseyClientBuilder.build()).thenReturn(client);
        when(jerseyClientBuilder.register(SseFeature.class)).thenReturn(jerseyClientBuilder);
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.request(any(MediaType.class))).thenReturn(builder);
        when(builder.get()).thenReturn(resonse);
        when(resonse.readEntity(String.class)).thenReturn("");

        ClientConfiguration clientConfiguration = new ClientConfiguration("test");

        Response<Object> response = uut.create("http://mercateo.com/test", Object.class,
                clientConfiguration);
        verify(jerseyClientBuilder).build();
        verify(client).target(anyString());
        verify(webTarget).request(any(MediaType.class));
        verify(builder).get();
        verify(resonse).readEntity(String.class);
        verify(client).register(eq(new AuthHeaderFilter("test")));
    }

}
