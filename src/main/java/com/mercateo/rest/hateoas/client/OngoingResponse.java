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

import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;

public interface OngoingResponse<T> {
	/**
	 * 
	 * @param rel
	 *            the name of the relation
	 * @return the returned object with schema
	 * @throws ProcessingException
	 *             generic exception when something went wrong
	 * @throws WebApplicationException
	 *             if server response status was greater than 300
	 * @throws IllegalStateException
	 *             if the template does not match the configured RequestObject
	 */
	Optional<Response<T>> callWithRel(String rel)
			throws ProcessingException, WebApplicationException, IllegalStateException;

	/**
	 * 
	 * @param rel
	 *            the name of the relation
	 * @return the returned list with schema
	 * @throws ProcessingException
	 *             generic exception when something went wrong
	 * @throws WebApplicationException
	 *             if server response status was greater than 300
	 * @throws IllegalStateException
	 *             if the template does not match the configured RequestObject
	 */
	public Optional<ListResponse<T>> callListWithRel(String rel)
			throws ProcessingException, WebApplicationException, IllegalStateException;

	OngoingResponse<T> withRequestObject(Object object);

	Optional<AutoCloseable> subscribe(String rel, SSEObserver<T> observer, String mainEventName, long reconnectionTime);

}
