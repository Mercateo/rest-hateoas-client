package com.mercateo.rest.hateoas.client.impl.sse;

import lombok.Getter;
import lombok.NonNull;

public class ParsingFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Getter
    private final String eventId;

    public ParsingFailedException(@NonNull Throwable cause, @NonNull String eventId) {
        super("Failed to parse event with id " + eventId, cause);
        this.eventId = eventId;
    }
}
