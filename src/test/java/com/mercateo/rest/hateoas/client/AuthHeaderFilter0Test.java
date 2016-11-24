package com.mercateo.rest.hateoas.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

public class AuthHeaderFilter0Test {
	@Test
	public void test() throws IOException {
		AuthHeaderFilter uut = new AuthHeaderFilter("test");
		ClientRequestContext clientRequestContext = mock(ClientRequestContext.class);
		MultivaluedMap<String, Object> value = mock(MultivaluedMap.class);
		when(clientRequestContext.getHeaders()).thenReturn(value);
		uut.filter(clientRequestContext);
		verify(value).putSingle(HttpHeaders.AUTHORIZATION, "test");
	}
}
