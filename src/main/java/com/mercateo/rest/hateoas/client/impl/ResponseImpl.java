package com.mercateo.rest.hateoas.client.impl;

import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;
import com.mercateo.rest.hateoas.client.OngoingResponse;
import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.schema.ClientHyperSchema;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ResponseImpl<T> implements Response<T> {
	@NonNull
	private final ResponseBuilder responseBuilder;

	@VisibleForTesting
	final ClientHyperSchema jsonHyperSchema;

	protected final T value;

	@Override
	public Optional<T> getResponseObject() {
		return Optional.ofNullable(value);
	}

	@Override
	public <S> OngoingResponse<S> prepareNextWithResponse(@NonNull Class<S> clazz) {
		if (jsonHyperSchema == null) {
			throw new IllegalStateException("There is no possibility for a next response");
		}
		return new OngoingResponseImpl<S>(clazz, jsonHyperSchema, responseBuilder);
	}

	@Override
	public boolean isRelPresent(@NonNull String rel) {
		return jsonHyperSchema != null && jsonHyperSchema.getByRel(rel).isPresent();
	}

}
