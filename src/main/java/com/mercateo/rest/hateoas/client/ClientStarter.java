/**
 * Copyright © 2016 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.rest.hateoas.client;

import java.net.URI;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyInvocation.Builder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.media.sse.SseFeature;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.mercateo.rest.hateoas.client.impl.ResponseBuilder;
import com.mercateo.rest.hateoas.client.impl.sse.LastEventIdHeaderFilter;

import lombok.NonNull;

public class ClientStarter {

	private final JerseyClientBuilder jerseyClientBuilder;

	private final ClientConfig clientConfig;

	@VisibleForTesting
	final ObjectMapper objectMapper;

	public ClientStarter() {
		this(new ObjectMapper());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public ClientStarter(@NonNull ObjectMapper objectMapper) {
		this(new JerseyClientBuilder(), objectMapper.copy());
	}

	@VisibleForTesting
	ClientStarter(@NonNull JerseyClientBuilder jerseyClientBuilder, @NonNull ObjectMapper objectMapper) {
		this.jerseyClientBuilder = jerseyClientBuilder;
		this.objectMapper = objectMapper;

		JaxbAnnotationModule module = new JaxbAnnotationModule();
		objectMapper.registerModule(module);
		this.clientConfig = createConfig();
	}

	private ClientConfig createConfig() {
		ClientConfig config = new ClientConfig();
		config.connectorProvider(new ApacheConnectorProvider());

		JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
		jacksonProvider.setMapper(objectMapper);
		config.register(jacksonProvider);
		return config;
	}

	public <RootResponse> Response<RootResponse> create(@NonNull String url, @NonNull Class<RootResponse> clazz) {
		return create(url, clazz, null);
	}

	public <RootResponse> Response<RootResponse> create(@NonNull String url, @NonNull Class<RootResponse> clazz,
			ClientConfiguration clientConfigurationOrNull) {
		Optional<ClientConfiguration> clientConfiguration = Optional.ofNullable(clientConfigurationOrNull);
		JerseyClient newClient = jerseyClientBuilder.register(SseFeature.class).withConfig(clientConfig).build();
		newClient.register(LastEventIdHeaderFilter.class);

		if (clientConfigurationOrNull != null && !Strings.isNullOrEmpty(clientConfigurationOrNull.getAuthorization())) {
			newClient.register(new AuthHeaderFilter(clientConfigurationOrNull.getAuthorization()));
		}

		MediaType mediaType = clientConfiguration.map(c -> c.getMediaType()).orElse(MediaType.APPLICATION_JSON_TYPE);

		ResponseBuilder responseBuilder = new ResponseBuilder(newClient, objectMapper, mediaType);
		JerseyWebTarget webTarget = newClient.target(url);
		Builder requestBuilder = webTarget.request(mediaType);
		String readEntity = requestBuilder.get().readEntity(String.class);
		return responseBuilder.buildResponse(readEntity, clazz, URI.create(url)).get();
	}

}
