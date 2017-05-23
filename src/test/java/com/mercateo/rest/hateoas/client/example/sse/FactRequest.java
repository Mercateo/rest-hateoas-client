package com.mercateo.rest.hateoas.client.example.sse;

import lombok.Value;

@Value
public class FactRequest {

    private boolean continuous;

    private String factSpec;
}
