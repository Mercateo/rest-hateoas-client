package com.mercateo.rest.hateoas.client;

import java.util.Optional;

import javax.ws.rs.ProcessingException;

public interface OngoingResponse<T> {

	Optional<Response<T>> callWithRel(String rel) throws ProcessingException;

	public Optional<ListResponse<T>> callListWithRel(String rel);

	OngoingResponse<T> withRequestObject(Object object);

}
