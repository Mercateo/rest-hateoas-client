package com.mercateo.rest.hateoas.client.schema;

import static org.junit.Assert.assertTrue;

import org.glassfish.jersey.uri.UriTemplate;
import org.junit.Test;

public class SchemaLink0Test {
	@Test
	public void testCorrectTemplate() {
		SchemaLink schemaLink = new SchemaLink();
		schemaLink.setHref("http://www.test.com/{id}");
		UriTemplate uriTemplate = schemaLink.getHref();
		assertTrue(uriTemplate.isTemplateVariablePresent("id"));
	}

	@Test
	public void testFallbackTemplate() {
		SchemaLink schemaLink = new SchemaLink();
		schemaLink.setHref("http://www.test.com/%7Bid%7D}");
		UriTemplate uriTemplate = schemaLink.getHref();
		assertTrue(uriTemplate.isTemplateVariablePresent("id"));
	}
}
