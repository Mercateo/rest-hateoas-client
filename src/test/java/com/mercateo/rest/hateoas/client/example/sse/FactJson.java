package com.mercateo.rest.hateoas.client.example.sse;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class FactJson {

    JsonNode header;

    JsonNode payload;
}
