package com.mercateo.rest.hateoas.client.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;

@RunWith(MockitoJUnitRunner.class)
public class ResponseImpl0Test {
	@Mock
	private JsonHyperSchema jsonHyperSchema;

	@Mock
	private ResponseBuilder responseBuilder;

	@Test
	public void testGetResponseObject() throws Exception {
		ResponseImpl<Object> uut = new ResponseImpl<Object>(responseBuilder, jsonHyperSchema, new Object());
		assertTrue(uut.getResponseObject().isPresent());
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
