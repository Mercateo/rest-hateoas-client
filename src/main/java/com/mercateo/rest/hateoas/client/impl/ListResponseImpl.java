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
package com.mercateo.rest.hateoas.client.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import com.mercateo.rest.hateoas.client.ListResponse;
import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.schema.ClientHyperSchema;

public class ListResponseImpl<T> extends ResponseImpl<List<Response<T>>>implements ListResponse<T> {

	public ListResponseImpl(ResponseBuilder responseBuilder, ClientHyperSchema jsonHyperSchema,
			List<Response<T>> value, URI uri) {
		super(responseBuilder, jsonHyperSchema, value, uri);
	}

	@Override
	public Optional<Response<T>> get(int index) {
		checkArgument(0 <= index);
		return Optional.ofNullable(value).map(l -> l.get(index));
	}
}
