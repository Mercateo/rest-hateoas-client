package com.mercateo.rest.hateoas.client;

public interface SSEObserver<T> {
    public void onEvent(Response<T> response);

    public void onSignal(String signal);
}
