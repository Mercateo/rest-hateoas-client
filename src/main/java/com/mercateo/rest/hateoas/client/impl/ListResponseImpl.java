package com.mercateo.rest.hateoas.client.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.rest.hateoas.client.ListResponse;
import com.mercateo.rest.hateoas.client.OngoingResponse;
import com.mercateo.rest.hateoas.client.Response;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ListResponseImpl<T> implements ListResponse<T> {
	@NonNull
	private ResponseBuilder responseBuilder;
	@VisibleForTesting
	JsonHyperSchema jsonHyperSchema;

	private List<Response<T>> value;

	@Override
	public Optional<List<T>> getResponseObject() {
		return Optional.ofNullable(value.stream().map(Response::getResponseObject).filter(Optional::isPresent)
				.map(Optional::get).collect(Collectors.toList()));
	}

	@Override
	public <S> OngoingResponse<S> prepareNextWithResponse(@NonNull Class<S> clazz) {
		return new OngoingResponseImpl<S>(clazz, jsonHyperSchema, responseBuilder);
	}

	@Override
	public Response<T> get(int index) {
		checkArgument(0 <= index);
		checkArgument(index < value.size());
		return value.get(index);
	}
}
