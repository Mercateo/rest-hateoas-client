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

import java.net.URI;
import java.util.Optional;

import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.InboundEvent;

import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.SSEObserver;
import com.mercateo.rest.hateoas.client.SSEObserver.ParseError;
import com.mercateo.rest.hateoas.client.impl.ResponseBuilder;

import lombok.NonNull;

public class SSEListener<T> implements EventListener {

	private Class<T> clazz;

	private SSEObserver<T> sseObserver;

	private ResponseBuilder responseBuilder;

	private String mainEventName;

	private URI uri;

	public SSEListener(@NonNull Class<T> clazz, @NonNull ResponseBuilder responseBuilder,
			@NonNull SSEObserver<T> sseObserver, @NonNull String mainEventName, URI uri) {
		this.mainEventName = mainEventName;
		this.clazz = clazz;
		this.responseBuilder = responseBuilder;
		this.sseObserver = sseObserver;
		this.uri = uri;
	}

	@Override
	public void onEvent(InboundEvent inboundEvent) {
		String eventName = inboundEvent.getName();
		if (mainEventName.equals(eventName)) {
			Optional<Response<T>> resp;
			try {
				resp = responseBuilder.buildResponse(inboundEvent.readData(), clazz, uri);
			} catch (Exception e) {
				sseObserver.onParseError(new ParseError(inboundEvent.getId(), e, inboundEvent.getRawData()));
				return;
			}
			sseObserver.onEvent(resp.get());
		} else {
			sseObserver.onSignal(eventName);
		}

	}

	void onConnectionError() {
		sseObserver.onConnectionError();
	}

}
