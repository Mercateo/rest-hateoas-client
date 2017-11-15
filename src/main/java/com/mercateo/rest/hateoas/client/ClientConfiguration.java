package com.mercateo.rest.hateoas.client;

import javax.ws.rs.core.MediaType;

import lombok.Value;

@Value
public class ClientConfiguration {
	String authorization;
	MediaType mediaType;

}
