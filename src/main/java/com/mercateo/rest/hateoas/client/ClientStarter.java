package com.mercateo.rest.hateoas.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.mercateo.rest.hateoas.client.impl.ResponseBuilder;

import lombok.NonNull;

public class ClientStarter {

	public static <RootResponse> Response<RootResponse> create(@NonNull String url,
			@NonNull Class<RootResponse> clazz) {
		Client newClient = ClientBuilder.newClient();
		ObjectMapper objectMapper = new ObjectMapper();
		JaxbAnnotationModule module = new JaxbAnnotationModule();

		objectMapper.registerModule(module);
		ResponseBuilder responseBuilder = new ResponseBuilder(newClient, objectMapper);
		return responseBuilder.buildResponse(
				newClient.target(url).request(MediaType.APPLICATION_JSON_TYPE).get().readEntity(String.class), clazz)
				.get();
	}
}
