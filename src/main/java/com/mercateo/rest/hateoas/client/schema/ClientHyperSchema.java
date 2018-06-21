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