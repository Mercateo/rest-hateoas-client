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

	/**
	 * this is called when some error occurred while parsing the main eventtype
	 * 
	 * @param e
	 *            the error
	 */
	public void onParseError(ParseError e);

	/**
	 * this is called when some connection error occurred. Due to
	 * https://github.com/jersey/jersey/issues/3537 it is not possible to
	 * distinguish between errors at this moment. The EventSource is closed! so
	 * no events will be published anymore
	 *
	 */
	public void onConnectionError();

	public default Optional<String> lastKnownEventId() {
		return Optional.empty();
	}

}
