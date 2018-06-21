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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.jersey.uri.UriTemplate;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

/**
 * the client representation for links. Note that href is an String because it
 * could be an uri or uri Template
 * 
 * @author joerg.adler
 *
 */
@Data
@JsonInclude(Include.NON_NULL)
public class SchemaLink {

	private UriTemplate href;

	private String rel;

	private Map<String, String> map = new HashMap<>();

	private JsonNode schema;

	private JsonNode targetSchema;

	private String mediaType;

	// backwards compatibility, would be canged in next version
	public void setHref(String href) {
		if (href.contains("%7B")) {
			try {
				href = URLDecoder.decode(href, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		this.href = new UriTemplate(href);
	}

	@JsonAnyGetter
	public Map<String, String> getMap() {
		return map;
	}

	@JsonAnySetter
	public void setMap(String key, String value) {
		map.put(key, value);
	}
}
