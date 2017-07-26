package com.mercateo.rest.hateoas.client;

import java.util.Optional;

public interface SSEObserver<T> {
    public void onEvent(Response<T> response);

    public void onSignal(String signal);

    /**
     * this method is called when some errors occurred. Due to
     * https://github.com/jersey/jersey/issues/3537 it is not possible to
     * distinguish between errors at this moment
     * 
     * @param errorCode
     */
    public void onError(String errorCode);

    public default Optional<String> lastKnownEventId() {
        return Optional.empty();
    }

}
