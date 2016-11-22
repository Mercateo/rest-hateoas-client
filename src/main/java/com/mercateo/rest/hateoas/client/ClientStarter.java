package com.mercateo.rest.hateoas.client;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.JerseyClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.common.annotations.VisibleForTesting;
import com.mercateo.rest.hateoas.client.impl.ResponseBuilder;

import lombok.NonNull;

public class ClientStarter {

	private final JerseyClientBuilder jerseyClientBuilder;

	public ClientStarter() {
		super();
		jerseyClientBuilder = new JerseyClientBuilder();
	}

	@VisibleForTesting
	@Inject
	ClientStarter(@NonNull JerseyClientBuilder jerseyClientBuilder) {
		super();
		this.jerseyClientBuilder = jerseyClientBuilder;
	}

	public <RootResponse> Response<RootResponse> create(@NonNull String url, @NonNull Class<RootResponse> clazz) {
		Client newClient = jerseyClientBuilder.build();
		ObjectMapper objectMapper = new ObjectMapper();
		JaxbAnnotationModule module = new JaxbAnnotationModule();

		objectMapper.registerModule(module);
		ResponseBuilder responseBuilder = new ResponseBuilder(newClient, objectMapper);
		return responseBuilder.buildResponse(
				newClient.target(url).request(MediaType.APPLICATION_JSON_TYPE).get().readEntity(String.class), clazz)
				.get();
	}

}
