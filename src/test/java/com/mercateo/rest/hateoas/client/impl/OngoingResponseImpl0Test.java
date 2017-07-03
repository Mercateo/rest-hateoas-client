package com.mercateo.rest.hateoas.client.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.message.internal.Statuses;
import org.glassfish.jersey.uri.UriTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Maps;
import com.mercateo.rest.hateoas.client.schema.ClientHyperSchema;
import com.mercateo.rest.hateoas.client.schema.SchemaLink;

import jersey.repackaged.com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public class OngoingResponseImpl0Test {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class StringIdBean {
        String id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class StringIdCollectionBean {
        String id;

        List<String> list;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public class StringIdBean2 extends StringIdBean {
        String id;
    }

    @Mock
    private ClientHyperSchema jsonHyperSchema;

    @Mock
    private ResponseBuilder responseBuilder;

    @Mock
    private Client client;

    @Mock
    private WebTarget webTarget;

    @Mock
    private Builder builder;

    @Mock
    private Response response;

    private OngoingResponseImpl<?> uut;

    private URI uri = URI.create("http://localhost:8080/");

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        uut = new OngoingResponseImpl<>(Object.class, jsonHyperSchema, responseBuilder, uri);
        when(responseBuilder.getClient()).thenReturn(client);
        when(client.target(any(URI.class))).thenReturn(webTarget);
        when(webTarget.queryParam(any(), anyVararg())).thenReturn(webTarget);

        when(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(builder);
        when(builder.method(any())).thenReturn(response);
        when(builder.method(any(), any(Entity.class))).thenReturn(response);
    }

    @Test
    public void testCallWithRel() throws Exception {
        SchemaLink link = new SchemaLink();
        link.setMap(Maps.asMap(Sets.newHashSet(OngoingResponseImpl.METHOD_PARAM_KEY), k -> "put"));
        String uri = "http://www.mercateo.com/";
        link.setHref(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(link));
        when(response.readEntity(String.class)).thenReturn("");
        uut.callWithRel("test");
        verify(responseBuilder).buildResponse(any(), any());
    }

    @Test
    public void testCallWithRelAndGetParams() throws Exception {
        SchemaLink link = new SchemaLink();
        link.setMap(Maps.asMap(Sets.newHashSet(OngoingResponseImpl.METHOD_PARAM_KEY), k -> "get"));
        String uri = "http://www.mercateo.com/";
        link.setHref(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(link));
        when(response.readEntity(String.class)).thenReturn("");
        StringIdCollectionBean sb = new StringIdCollectionBean();
        sb.setId("1");
        sb.setList(Arrays.asList("1", "2"));
        uut.withRequestObject(sb).callWithRel("test");
        verify(responseBuilder).buildResponse(any(), any());
        verify(webTarget).queryParam("id", "1");
        verify(webTarget).queryParam("list", "1", "2");
    }

    @Test
    public void testCallWithRelAndTemplate() throws Exception {
        SchemaLink mockLink = mock(SchemaLink.class);
        when(mockLink.getMap()).thenReturn(Maps.asMap(Sets.newHashSet(
                OngoingResponseImpl.METHOD_PARAM_KEY), k -> "get"));
        UriTemplate uri = new UriTemplate("http://www.mercateo.com/{id}");
        when(mockLink.getHref()).thenReturn(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
        when(response.readEntity(String.class)).thenReturn("");
        uut = uut.withRequestObject(new StringIdBean("id1"));
        uut.callWithRel("test");
        verify(responseBuilder).buildResponse(any(), any());
        verify(client).target(eq(new URI("http://www.mercateo.com/id1")));
    }

    @Test
    public void testCallWithRelAndTemplate_to_many_parameter_for_template() throws Exception {
        SchemaLink mockLink = mock(SchemaLink.class);
        when(mockLink.getMap()).thenReturn(Maps.asMap(Sets.newHashSet(
                OngoingResponseImpl.METHOD_PARAM_KEY), k -> "get"));
        UriTemplate uri = new UriTemplate("http://www.mercateo.com/{id}");
        when(mockLink.getHref()).thenReturn(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
        when(response.readEntity(String.class)).thenReturn("");
        uut = uut.withRequestObject(new StringIdBean2());
        try {
            uut.callWithRel("test");
            Assert.fail("No IllegalStateException thrown");
        } catch (IllegalStateException e) {
            assertEquals(
                    "There is more than one field for the template variable id: [java.lang.String com.mercateo.rest.hateoas.client.impl.OngoingResponseImpl0Test$StringIdBean.id, java.lang.String com.mercateo.rest.hateoas.client.impl.OngoingResponseImpl0Test$StringIdBean2.id]",
                    e.getMessage());
        }
    }

    @Test
    public void testCallWithRelAndTemplate_missing_parameter_for_template_depr() throws Exception {
        SchemaLink mockLink = mock(SchemaLink.class);
        when(mockLink.getMap()).thenReturn(Maps.asMap(Sets.newHashSet(
                OngoingResponseImpl.METHOD_PARAM_KEY), k -> "get"));
        UriTemplate uri = new UriTemplate("http://www.mercateo.com/{id}");
        when(mockLink.getHref()).thenReturn(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
        when(response.readEntity(String.class)).thenReturn("");
        uut = uut.withRequestObject(new Object());
        try {
            uut.callWithRel("test");
            Assert.fail("No IllegalStateException thrown");
        } catch (IllegalStateException e) {
            assertEquals("No field found for the template variable id", e.getMessage());
        }
    }

    @Test
    public void testCallWithRelAndTemplate_missing_parameter_for_template() throws Exception {
        SchemaLink mockLink = mock(SchemaLink.class);
        when(mockLink.getMap()).thenReturn(Maps.asMap(Sets.newHashSet(
                OngoingResponseImpl.METHOD_PARAM_KEY), k -> "get"));
        UriTemplate uri = new UriTemplate("http://www.mercateo.com/{id}");
        when(mockLink.getHref()).thenReturn(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
        when(response.readEntity(String.class)).thenReturn("");
        uut = uut.withRequestObject(new Object());
        try {
            uut.callWithRel("test");
            Assert.fail("No IllegalStateException thrown");
        } catch (IllegalStateException e) {
            assertEquals("No field found for the template variable id", e.getMessage());
        }
    }

    @Test(expected = WebApplicationException.class)
    public void testCallWithRel_badRequest() throws Exception {
        SchemaLink mockLink = mock(SchemaLink.class);
        when(mockLink.getMap()).thenReturn(Maps.asMap(Sets.newHashSet(
                OngoingResponseImpl.METHOD_PARAM_KEY), k -> "put"));
        UriTemplate uri = new UriTemplate("http://www.mercateo.com/");
        when(mockLink.getHref()).thenReturn(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
        when(response.readEntity(String.class)).thenReturn("");
        when(response.getStatus()).thenReturn(400);
        when(response.getStatusInfo()).thenReturn(Statuses.from(400));
        uut.callWithRel("test");
    }

    @Test
    public void testCallWithRel_Object() throws Exception {
        SchemaLink mockLink = mock(SchemaLink.class);
        when(mockLink.getMap()).thenReturn(Maps.asMap(Sets.newHashSet(
                OngoingResponseImpl.METHOD_PARAM_KEY), k -> "put"));
        UriTemplate uri = new UriTemplate("http://www.mercateo.com/");
        when(mockLink.getHref()).thenReturn(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
        when(response.readEntity(String.class)).thenReturn("");
        uut = uut.withRequestObject(new Object());
        uut.callWithRel("test");
        verify(builder).method(any(), any(Entity.class));
        verify(responseBuilder).buildResponse(any(), any());
    }

    @Test
    public void testDoNotPassEntityForGet() throws Exception {
        SchemaLink mockLink = mock(SchemaLink.class);
        when(mockLink.getMap()).thenReturn(Maps.asMap(Sets.newHashSet(
                OngoingResponseImpl.METHOD_PARAM_KEY), k -> "get"));
        UriTemplate uri = new UriTemplate("http://www.mercateo.com/%7Bid%7D");
        when(mockLink.getHref()).thenReturn(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
        when(response.readEntity(String.class)).thenReturn("");
        uut = uut.withRequestObject(new StringIdBean("bla"));
        uut.callWithRel("test");
        verify(builder, never()).method(any(), any(Entity.class));
    }

    @Test
    public void testCallListWithRel() throws Exception {
        SchemaLink mockLink = mock(SchemaLink.class);
        when(mockLink.getMap()).thenReturn(Maps.asMap(Sets.newHashSet(
                OngoingResponseImpl.METHOD_PARAM_KEY), k -> "put"));
        UriTemplate uri = new UriTemplate("http://www.mercateo.com/");
        when(mockLink.getHref()).thenReturn(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
        when(response.readEntity(String.class)).thenReturn("");
        uut.callListWithRel("test");
        verify(responseBuilder).buildListResponse(any(), any());
    }

    @Test
    public void testCallWithRelNotExist() throws Exception {
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.empty());
        when(response.readEntity(String.class)).thenReturn("");
        Optional<?> returnValue = uut.callWithRel("test");
        assertFalse(returnValue.isPresent());
        verifyNoMoreInteractions(responseBuilder);
    }

    @Test
    public void testCallListWithRelNotExist() throws Exception {
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.empty());
        when(response.readEntity(String.class)).thenReturn("");
        Optional<?> returnValue = uut.callListWithRel("test");
        assertFalse(returnValue.isPresent());
        verifyNoMoreInteractions(responseBuilder);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ProcessingException.class)
    public void testCallWithRel_SchemaParseException() throws Exception {
        SchemaLink mockLink = mock(SchemaLink.class);
        when(mockLink.getMap()).thenReturn(Maps.asMap(Sets.newHashSet(
                OngoingResponseImpl.METHOD_PARAM_KEY), k -> "put"));
        UriTemplate uri = new UriTemplate("http://www.mercateo.com/");
        when(mockLink.getHref()).thenReturn(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
        when(response.readEntity(String.class)).thenThrow(ProcessingException.class);

        uut.callWithRel("test");
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ProcessingException.class)
    public void testCallListWithRel_SchemaParseException() throws Exception {
        SchemaLink mockLink = mock(SchemaLink.class);
        when(mockLink.getMap()).thenReturn(Maps.asMap(Sets.newHashSet(
                OngoingResponseImpl.METHOD_PARAM_KEY), k -> "put"));
        UriTemplate uri = new UriTemplate("http://www.mercateo.com/");
        when(mockLink.getHref()).thenReturn(uri);
        when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
        when(response.readEntity(String.class)).thenThrow(ProcessingException.class);
        uut.callListWithRel("test");
    }
}
