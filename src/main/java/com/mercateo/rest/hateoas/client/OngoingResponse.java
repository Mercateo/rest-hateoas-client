package com.mercateo.rest.hateoas.client;

import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;

public interface OngoingResponse<T> {

	Optional<Response<T>> callWithRel(String rel) throws ProcessingException, WebApplicationException;

	public Optional<ListResponse<T>> callListWithRel(String rel) throws ProcessingException, WebApplicationException;

	OngoingResponse<T> withRequestObject(Object object);

}
