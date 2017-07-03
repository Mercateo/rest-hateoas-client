package com.mercateo.rest.hateoas.client.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.mercateo.rest.hateoas.client.ListResponse;
import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.example.OrderProjectionJson;

@RunWith(MockitoJUnitRunner.class)
public class ResponseBuilder0Test {
	@Mock
	private Client client;

	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();

	private ResponseBuilder uut;

    private URI uri = URI.create("http://localhost:8080/");

	@Before
	public void setup() {
		JaxbAnnotationModule module = new JaxbAnnotationModule();
		objectMapper.registerModule(module);
		uut = new ResponseBuilder(client, objectMapper, uri);
	}

	@Test
	public void testBuildEmptyResponse() throws Exception {
		Optional<Response<Object>> resp = uut.buildResponse("", Object.class);
		assertFalse(resp.get().getResponseObject().isPresent());
	}

	@Test
	public void testBuildResponse() throws Exception {
		String responseRaw = CharStreams
				.toString(new InputStreamReader(this.getClass().getResourceAsStream("response.json"), Charsets.UTF_8));

		Optional<Response<Object>> resp = uut.buildResponse(responseRaw, Object.class);
		assertTrue(resp.get().getResponseObject().isPresent());
		ResponseImpl<Object> r = (ResponseImpl<Object>) resp.get();
		assertNotNull(r.jsonHyperSchema);
		assertTrue(r.jsonHyperSchema.getByRel("orders").isPresent());
		assertTrue(r.jsonHyperSchema.getByRel("orders-linking").isPresent());
	}

	@Test(expected = ProcessingException.class)
	public void testBuildResponseSchemaNotMatch() throws Exception {
		String responseRaw = CharStreams
				.toString(new InputStreamReader(this.getClass().getResourceAsStream("response.json"), Charsets.UTF_8));
		uut.buildResponse(responseRaw, String.class);
	}

	@Test
	public void testBuildListResponse() throws Exception {
		String responseRaw = CharStreams.toString(
				new InputStreamReader(this.getClass().getResourceAsStream("listresponse.json"), Charsets.UTF_8));

		Optional<ListResponse<OrderProjectionJson>> resp = uut.buildListResponse(responseRaw,
				OrderProjectionJson.class);
		assertTrue(resp.get().getResponseObject().isPresent());
		ListResponseImpl<OrderProjectionJson> r = (ListResponseImpl<OrderProjectionJson>) resp.get();
		assertNotNull(r.jsonHyperSchema);
		assertTrue(r.jsonHyperSchema.getByRel("self").isPresent());
		ResponseImpl<OrderProjectionJson> orderResp = (ResponseImpl<OrderProjectionJson>) r.get(1).get();

		assertEquals(200d, orderResp.getResponseObject().get().getTotalAmount(), 0);

		assertTrue(orderResp.jsonHyperSchema.getByRel("send-back").isPresent());
	}

	@Test(expected = ProcessingException.class)
	public void testBuildListResponse_No_Members() throws Exception {
		String responseRaw = CharStreams
				.toString(new InputStreamReader(this.getClass().getResourceAsStream("response.json"), Charsets.UTF_8));

		uut.buildListResponse(responseRaw, OrderProjectionJson.class);
	}

}
