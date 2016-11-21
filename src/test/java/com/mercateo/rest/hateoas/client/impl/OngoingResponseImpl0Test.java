package com.mercateo.rest.hateoas.client.impl;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.message.internal.Statuses;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Maps;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.common.rest.schemagen.link.LinkCreator;

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
	@EqualsAndHashCode(callSuper = true)
	public class StringIdBean2 extends StringIdBean {
		String id;
	}

	@Mock
	private JsonHyperSchema jsonHyperSchema;

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

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		uut = new OngoingResponseImpl<>(Object.class, jsonHyperSchema, responseBuilder);
		when(responseBuilder.getClient()).thenReturn(client);
		when(client.target(any(URI.class))).thenReturn(webTarget);
		when(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(builder);
		when(builder.method(any())).thenReturn(response);
		when(builder.method(any(), any(Entity.class))).thenReturn(response);
	}

	@Test
	public void testCallWithRel() throws Exception {
		Link mockLink = mock(Link.class);
		when(mockLink.getParams()).thenReturn(Maps.asMap(Sets.newHashSet(LinkCreator.METHOD_PARAM_KEY), k -> "put"));
		when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
		when(response.readEntity(String.class)).thenReturn("");
		uut.callWithRel("test");
		verify(responseBuilder).buildResponse(any(), any());
	}

	@Test
	public void testCallWithRelAndTemplate() throws Exception {
		Link mockLink = mock(Link.class);
		when(mockLink.getParams()).thenReturn(Maps.asMap(Sets.newHashSet(LinkCreator.METHOD_PARAM_KEY), k -> "get"));
		URI uri = new URI("http://www.mercateo.com/%7Bid%7D");
		UriBuilder uriBuilder = UriBuilder.fromUri(uri);
		when(mockLink.getUri()).thenReturn(uri);
		when(mockLink.getUriBuilder()).thenReturn(uriBuilder);
		when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
		when(response.readEntity(String.class)).thenReturn("");
		uut = uut.withRequestObject(new StringIdBean("id1"));
		uut.callWithRel("test");
		verify(responseBuilder).buildResponse(any(), any());
		verify(client).target(eq(new URI("http://www.mercateo.com/id1")));
	}

	@Test(expected = IllegalStateException.class)
	public void testCallWithRelAndTemplate_wrongParameterObject() throws Exception {
		Link mockLink = mock(Link.class);
		when(mockLink.getParams()).thenReturn(Maps.asMap(Sets.newHashSet(LinkCreator.METHOD_PARAM_KEY), k -> "get"));
		URI uri = new URI("http://www.mercateo.com/%7Bid%7D");
		UriBuilder uriBuilder = UriBuilder.fromUri(uri);
		when(mockLink.getUri()).thenReturn(uri);
		when(mockLink.getUriBuilder()).thenReturn(uriBuilder);
		when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
		when(response.readEntity(String.class)).thenReturn("");
		uut = uut.withRequestObject(new StringIdBean2());
		uut.callWithRel("test");
	}

	@Test(expected = WebApplicationException.class)
	public void testCallWithRel_badRequest() throws Exception {
		Link mockLink = mock(Link.class);
		when(mockLink.getParams()).thenReturn(Maps.asMap(Sets.newHashSet(LinkCreator.METHOD_PARAM_KEY), k -> "put"));
		when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
		when(response.readEntity(String.class)).thenReturn("");
		when(response.getStatus()).thenReturn(400);
		when(response.getStatusInfo()).thenReturn(Statuses.from(400));
		uut.callWithRel("test");
	}

	@Test
	public void testCallWithRel_Object() throws Exception {
		Link mockLink = mock(Link.class);
		when(mockLink.getParams()).thenReturn(Maps.asMap(Sets.newHashSet(LinkCreator.METHOD_PARAM_KEY), k -> "put"));
		when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
		when(response.readEntity(String.class)).thenReturn("");
		uut = uut.withRequestObject(new Object());
		uut.callWithRel("test");
		verify(builder).method(any(), any(Entity.class));
		verify(responseBuilder).buildResponse(any(), any());
	}

	@Test
	public void testCallListWithRel() throws Exception {
		Link mockLink = mock(Link.class);
		when(mockLink.getParams()).thenReturn(Maps.asMap(Sets.newHashSet(LinkCreator.METHOD_PARAM_KEY), k -> "put"));
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
		Link mockLink = mock(Link.class);
		when(mockLink.getParams()).thenReturn(Maps.asMap(Sets.newHashSet(LinkCreator.METHOD_PARAM_KEY), k -> "put"));
		when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
		when(response.readEntity(String.class)).thenThrow(ProcessingException.class);
		uut.callWithRel("test");
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ProcessingException.class)
	public void testCallListWithRel_SchemaParseException() throws Exception {
		Link mockLink = mock(Link.class);
		when(mockLink.getParams()).thenReturn(Maps.asMap(Sets.newHashSet(LinkCreator.METHOD_PARAM_KEY), k -> "put"));
		when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mockLink));
		when(response.readEntity(String.class)).thenThrow(ProcessingException.class);
		uut.callListWithRel("test");
	}
}
