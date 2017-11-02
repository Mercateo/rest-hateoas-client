package com.mercateo.rest.hateoas.client;

import java.util.Optional;

import lombok.NonNull;
import lombok.Value;

public interface SSEObserver<T> {
	@Value
	public static class ParseError {
		String eventId;

		@NonNull
		Exception cause;

		byte[] body;
	}

	public void onEvent(Response<T> response);

	public void onSignal(String signal);

	public void onParseError(ParseError e);

	/**
	 * this is called when some errors connection error occurred. Due to
	 * https://github.com/jersey/jersey/issues/3537 it is not possible to
	 * distinguish between errors at this moment
	 * 
	 * @param errorCode
	 */
	public void onConnectionError();

	public default Optional<String> lastKnownEventId() {
		return Optional.empty();
	}

}
