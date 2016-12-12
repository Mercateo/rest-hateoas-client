package com.mercateo.rest.hateoas.client.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class ClientHyperSchema0Test {

	private SchemaLink link1;

	private SchemaLink link2;

	private ClientHyperSchema hyperSchema;

	@Before
	public void setUp() {
		link1 = new SchemaLink();
		link2 = new SchemaLink();
		link1.setRel("rel1");
		link2.setRel("rel2");
		hyperSchema = new ClientHyperSchema(Arrays.asList(link1, link2));
	}

	@Test
	public void shouldReturnEmptyResultWhenLinkByRelIsNotFound() {
		assertFalse(hyperSchema.getByRel("unknown-rel").isPresent());
	}

	@Test
	public void shouldResultWhenLinkByRelIsFound() {
		assertTrue(hyperSchema.getByRel("rel1").isPresent());
	}
}
