package com.mercateo.rest.hateoas.client.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mercateo.rest.hateoas.client.schema.ClientHyperSchema;
import com.mercateo.rest.hateoas.client.schema.SchemaLink;

@RunWith(MockitoJUnitRunner.class)
public class ResponseImpl0Test {
	@Mock
	private ClientHyperSchema jsonHyperSchema;

	@Mock
	private ResponseBuilder responseBuilder;

    private URI uri = URI.create("http://localhost:8080/");

	@Test
	public void testGetResponseObject() throws Exception {
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, jsonHyperSchema, new Object(), uri);
		assertTrue(uut.getResponseObject().isPresent());
	}

	@Test
	public void testIsRelPresent() throws Exception {
		when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mock(SchemaLink.class)));
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, jsonHyperSchema, new Object(), uri);
		assertTrue(uut.isRelPresent("test"));
	}

	@Test
	public void testIsRelNotPresent() throws Exception {
		when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.empty());
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, jsonHyperSchema, new Object(), uri);
		assertFalse(uut.isRelPresent("test"));
	}

	@Test
	public void testIsRelNotPresent_noSchema() throws Exception {
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, null, new Object(), uri);
		assertFalse(uut.isRelPresent("test"));
	}

	@Test
	public void testGetNoResponseObject() throws Exception {
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, jsonHyperSchema, null, uri);
		assertFalse(uut.getResponseObject().isPresent());
	}

	@Test
	public void testPrepareNextWithResponse() throws Exception {
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, jsonHyperSchema, new Object(), uri);
		uut.prepareNextWithResponse(Object.class);
	}

	@Test(expected = IllegalStateException.class)
	public void testPrepareNextWithResponseNoSchema() throws Exception {
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, null, new Object(), uri);
		uut.prepareNextWithResponse(Object.class);
	}

}
