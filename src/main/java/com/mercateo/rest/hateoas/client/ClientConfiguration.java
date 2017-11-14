package com.mercateo.rest.hateoas.client;

import java.util.Optional;

import javax.ws.rs.core.MediaType;

import lombok.Value;

@Value
public class ClientConfiguration {
	String authorization;
	MediaType mediaType;

	public Optional<MediaType> getMediaType() {
		return Optional.ofNullable(mediaType);
	}

}
