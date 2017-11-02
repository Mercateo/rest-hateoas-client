package com.mercateo.rest.hateoas.client.impl.sse;

import java.util.Timer;
import java.util.TimerTask;

import org.glassfish.jersey.media.sse.EventSource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EventSourceWithCloseGuard implements AutoCloseable {
	@NonNull
	private EventSource eventSource;

	@NonNull
	private long reconnectionTime;

	@NonNull
	private SSEListener<?> sseListener;

	private Timer timer = new Timer(true);

	public void open() {
		eventSource.open();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (!eventSource.isOpen()) {
					sseListener.onConnectionError();
					timer.cancel();
				}

			}
		}, reconnectionTime, reconnectionTime / 2 + 1);
	}

	@Override
	public void close() {
		eventSource.close();
		timer.cancel();
	}
}
