package com.mercateo.rest.hateoas.client.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

	@Test
	public void testGetResponseObject() throws Exception {
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, jsonHyperSchema, new Object());
		assertTrue(uut.getResponseObject().isPresent());
	}

	@Test
	public void testIsRelPresent() throws Exception {
		when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.of(mock(SchemaLink.class)));
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, jsonHyperSchema, new Object());
		assertTrue(uut.isRelPresent("test"));
	}

	@Test
	public void testIsRelNotPresent() throws Exception {
		when(jsonHyperSchema.getByRel(any())).thenReturn(Optional.empty());
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, jsonHyperSchema, new Object());
		assertFalse(uut.isRelPresent("test"));
	}

	@Test
	public void testIsRelNotPresent_noSchema() throws Exception {
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, null, new Object());
		assertFalse(uut.isRelPresent("test"));
	}

	@Test
	public void testGetNoResponseObject() throws Exception {
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, jsonHyperSchema, null);
		assertFalse(uut.getResponseObject().isPresent());
	}

	@Test
	public void testPrepareNextWithResponse() throws Exception {
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, jsonHyperSchema, new Object());
		uut.prepareNextWithResponse(Object.class);
	}

	@Test(expected = IllegalStateException.class)
	public void testPrepareNextWithResponseNoSchema() throws Exception {
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, null, new Object());
		uut.prepareNextWithResponse(Object.class);
	}

}
