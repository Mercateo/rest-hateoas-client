package com.mercateo.rest.hateoas.client;

import java.util.Optional;

public interface Response<T> {

	Optional<T> getResponseObject();

	boolean isRelPresent(String rel);

	<S> OngoingResponse<S> prepareNextWithResponse(Class<S> clazz) throws IllegalStateException;
}
