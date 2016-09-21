package com.mercateo.rest.hateoas.client.impl;

import java.util.Optional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.common.rest.schemagen.link.LinkCreator;
import com.mercateo.common.rest.schemagen.link.relation.Relation;
import com.mercateo.rest.hateoas.client.ListResponse;
import com.mercateo.rest.hateoas.client.OngoingResponse;
import com.mercateo.rest.hateoas.client.Response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OngoingResponseImpl<S> implements OngoingResponse<S> {
	@Wither
	private Object requestObject;

	@NonNull
	private Class<S> responseClass;

	@NonNull
	private JsonHyperSchema jsonHyperSchema;

	@NonNull
	private ResponseBuilder responseBuilder;

	@Override
	public Optional<Response<S>> callWithRel(@NonNull String rel) {

		String responseString = getResponse(rel);
		if (responseString == null) {
			return Optional.empty();
		}

		return responseBuilder.buildResponse(responseString, responseClass);

	}

	@Override
	public Optional<ListResponse<S>> callListWithRel(@NonNull String rel) {
		String responseString = getResponse(rel);
		if (responseString == null) {
			return Optional.empty();
		}
		return responseBuilder.buildListResponse(responseString, responseClass);
	}

	private String getResponse(String rel) {
		Optional<Link> linkOption = jsonHyperSchema.getByRel(() -> Relation.of(rel));
		if (!linkOption.isPresent()) {
			return null;
		}
		Link link = linkOption.get();
		WebTarget target = responseBuilder.getClient().target(link);
		Builder b = target.request(MediaType.APPLICATION_JSON_TYPE);
		String method = link.getParams().get(LinkCreator.METHOD_PARAM_KEY);
		javax.ws.rs.core.Response response;

		if (requestObject != null) {
			response = b.method(method, Entity.json(requestObject));
		} else {
			response = b.method(method);
		}
		String responseString = response.readEntity(String.class);
		return responseString;
	}
}
