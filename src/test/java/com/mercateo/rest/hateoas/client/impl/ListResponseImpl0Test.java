package com.mercateo.rest.hateoas.client.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.schema.ClientHyperSchema;

import jersey.repackaged.com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class ListResponseImpl0Test {

    @Mock
	private ClientHyperSchema rawValue;

	@Mock
	private ResponseBuilder responseBuilder;

	@SuppressWarnings("unchecked")
	private Response<Object> response = mock(Response.class);

	@Spy
	private List<Response<?>> value = Lists.newArrayList(response);

	private URI uri = URI.create("http://localhost:8080/");

	private ListResponseImpl<?> uut;

	@Before
	public void setup() {
	    this.uut = new ListResponseImpl(responseBuilder, rawValue, value, uri);
	}

	@Test
	public void testPrepareNextWithResponse() throws Exception {
		uut.prepareNextWithResponse(Object.class);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetIllegalIndex() throws Exception {
		when(value.get(0)).thenThrow(IndexOutOfBoundsException.class);
		uut.get(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetIllegalNegativeIndex() throws Exception {
		uut.get(-1);
	}

	@Test
	public void testGet() throws Exception {
		when(value.size()).thenReturn(2);
		uut.get(0);
		verify(value).get(0);
	}

}
