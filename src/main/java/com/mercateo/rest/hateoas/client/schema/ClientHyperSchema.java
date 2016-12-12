package com.mercateo.rest.hateoas.client.schema;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;

import lombok.Data;
import lombok.NonNull;

@Data
public class ClientHyperSchema {
	private List<SchemaLink> links;

	public ClientHyperSchema(@JsonProperty("links") @NonNull Collection<SchemaLink> links) {
		this.links = links.stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	@VisibleForTesting
	public Optional<SchemaLink> getByRel(String rel) {
		return links.stream().filter(e -> e.getRel().equals(rel)).findFirst();
	}

}