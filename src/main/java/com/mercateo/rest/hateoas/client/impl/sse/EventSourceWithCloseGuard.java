/**
 * Copyright © 2016 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	private final Object closeLock = new Object();

	private boolean isShuttingDown = false;

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
				synchronized (closeLock) {
					if (!eventSource.isOpen() && !isShuttingDown) {
						try {
							close();
						} finally {
							sseListener.onConnectionError();
						}

					}
				}

			}
		}, reconnectionTime, reconnectionTime / 2 + 1);
	}

	@Override
	public void close() {
		synchronized (closeLock) {
			isShuttingDown = true;
			timer.cancel();
			eventSource.close();
		}
	}
}
