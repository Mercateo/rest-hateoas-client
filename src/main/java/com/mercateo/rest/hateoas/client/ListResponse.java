package com.mercateo.rest.hateoas.client;

import java.util.List;

public interface ListResponse<T> extends Response<List<T>> {

	public Response<T> get(int index);

}
