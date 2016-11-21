package com.mercateo.rest.hateoas.client;

import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;

public interface OngoingResponse<T> {
	/**
	 * 
	 * @param rel
	 * @return
	 * @throws ProcessingException
	 *             generic exception when something went wrong
	 * @throws WebApplicationException
	 *             if server response status was > 300
	 * @throws IllegalStateException
	 *             if the template does not match the configured RequestObject
	 */
	Optional<Response<T>> callWithRel(String rel)
			throws ProcessingException, WebApplicationException, IllegalStateException;

	/**
	 * 
	 * @param rel
	 * @return
	 * @throws ProcessingException
	 *             generic exception when something went wrong
	 * @throws WebApplicationException
	 *             if server response status was > 300
	 * @throws IllegalStateException
	 *             if the template does not match the configured RequestObject
	 */
	public Optional<ListResponse<T>> callListWithRel(String rel)
			throws ProcessingException, WebApplicationException, IllegalStateException;

	OngoingResponse<T> withRequestObject(Object object);

}
