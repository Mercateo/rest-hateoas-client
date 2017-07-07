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
