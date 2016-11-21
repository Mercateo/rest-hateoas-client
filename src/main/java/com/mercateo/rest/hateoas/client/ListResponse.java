package com.mercateo.rest.hateoas.client;

import java.util.List;
import java.util.Optional;

public interface ListResponse<T> extends Response<List<Response<T>>> {

	public Optional<Response<T>> get(int index) throws IllegalArgumentException, IndexOutOfBoundsException;

}
