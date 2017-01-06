package com.mercateo.rest.hateoas.client;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
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
		return create(url, clazz, null);
	}

	public <RootResponse> Response<RootResponse> create(@NonNull String url, @NonNull Class<RootResponse> clazz,
			ClientConfiguration clientConfigurationOrNull) {
		JerseyClient newClient = jerseyClientBuilder.build();
		if (clientConfigurationOrNull != null && !Strings.isNullOrEmpty(clientConfigurationOrNull.getAuthorization())) {
			newClient.register(new AuthHeaderFilter(clientConfigurationOrNull.getAuthorization()));
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JaxbAnnotationModule module = new JaxbAnnotationModule();

		objectMapper.registerModule(module);
		ResponseBuilder responseBuilder = new ResponseBuilder(newClient, objectMapper);
		return responseBuilder.buildResponse(
				newClient.target(url).request(MediaType.APPLICATION_JSON_TYPE).get().readEntity(String.class), clazz)
				.get();
	}

}
