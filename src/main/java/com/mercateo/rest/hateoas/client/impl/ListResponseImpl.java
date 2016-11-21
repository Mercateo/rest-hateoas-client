package com.mercateo.rest.hateoas.client.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Optional;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.rest.hateoas.client.ListResponse;
import com.mercateo.rest.hateoas.client.Response;

public class ListResponseImpl<T> extends ResponseImpl<List<Response<T>>>implements ListResponse<T> {

	public ListResponseImpl(ResponseBuilder responseBuilder, JsonHyperSchema jsonHyperSchema, List<Response<T>> value) {
		super(responseBuilder, jsonHyperSchema, value);
	}

	@Override
	public Optional<Response<T>> get(int index) {
		checkArgument(0 <= index);
		return Optional.ofNullable(value).map(l -> l.get(index));
	}
}
