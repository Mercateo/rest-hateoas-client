package com.mercateo.rest.hateoas.client.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercateo.rest.hateoas.client.ListResponse;
import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.schema.ClientHyperSchema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public class ResponseBuilder {

	@NonNull
	@Getter
	private Client client;

	@NonNull
	private ObjectMapper objectMapper;

	@NonNull
	@Getter
	private MediaType mediaType;

	public <S> Optional<Response<S>> buildResponse(@NonNull String responseString, @NonNull Class<S> responseClass,
			@NonNull URI uri) {
		if (responseString.length() == 0) {
			return Optional.of(new ResponseImpl<>(this, null, null, uri));
		}
		JsonNode rawValue = getRawValue(responseString);
		return buildSingleResponse(rawValue, responseClass, uri);
	}

	public <S> Optional<ListResponse<S>> buildListResponse(@NonNull String responseString,
			@NonNull Class<S> responseClass, @NonNull URI uri) {
		JsonNode rawValue = getRawValue(responseString);
		ClientHyperSchema jsonHyperSchema = buildSchema(rawValue);
		JsonNode membersNode = rawValue.get("members");
		if (membersNode != null) {
			List<Response<S>> list = new LinkedList<>();
			for (Iterator<JsonNode> iterator = membersNode.elements(); iterator.hasNext();) {
				JsonNode jsonNode = iterator.next();
				list.add(buildSingleResponse(jsonNode, responseClass, uri).get());
			}
			return Optional.of(new ListResponseImpl<>(this, jsonHyperSchema, list, uri));
		} else {
			throw new ProcessingException("There is no members field in the response");
		}
	}

	private ClientHyperSchema buildSchema(JsonNode rawValue) {
		JsonNode schemaElement = rawValue.get("_schema");
		if (schemaElement == null) {
		    throw new IllegalStateException("there is no '_schema'");
		}
		try {
			return objectMapper.treeToValue(schemaElement, ClientHyperSchema.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}

	private JsonNode getRawValue(String responseString) {
		JsonNode rawValue;
		try {
			rawValue = objectMapper.readTree(responseString);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return rawValue;
	}

	private <S> Optional<Response<S>> buildSingleResponse(JsonNode rawValue, Class<S> responseClass, URI uri) {
		try {

			S value = objectMapper.treeToValue(rawValue, responseClass);
			ClientHyperSchema schema = buildSchema(rawValue);
			Response<S> returningResponse = new ResponseImpl<>(this, schema, value, uri);
			return Optional.of(returningResponse);
		} catch (IOException e) {
			throw new ProcessingException("The response class " + responseClass.getName()
					+ " does not fit the response. Response was :" + rawValue, e);
		}
	}
}
