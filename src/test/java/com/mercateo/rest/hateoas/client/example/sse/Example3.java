package com.mercateo.rest.hateoas.client.example.sse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercateo.rest.hateoas.client.ClientStarter;
import com.mercateo.rest.hateoas.client.Response;

import lombok.Data;

public class Example3 {

    public static void main(String[] args) throws JsonProcessingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        FactTransaction factTransaction = new FactTransaction();
        for (int i = 0; i < 100000; i++) {
            FactJson fact = createFact(objectMapper);
            factTransaction.add(fact);
        }
        Response<Object> rootResource = new ClientStarter().create("http://localhost:8080",
                Object.class);
        rootResource.prepareNextWithResponse(Void.class).withRequestObject(factTransaction)
                .callWithRel("http://rels.factcast.org/create-transactional");
    }

    private static FactJson createFact(ObjectMapper objectMapper) throws IOException,
            JsonProcessingException {
        JsonNode header = objectMapper.readTree("{" + "\"id\" : \"" + UUID.randomUUID() + "\","
                + "\"ns\" : \"a\"," + "\"type\" : \"a\","
                + "\"aggIds\" : [ \"5ef49698-cfd3-4144-8399-91cfc2a1529f\" ]" + "}");
        JsonNode payload = objectMapper.readTree("{\"data\" : \"data\"}");
        FactJson fact = new FactJson();
        fact.header = header;
        fact.payload = payload;
        return fact;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class FactTransaction {
        private List<FactJson> facts = new ArrayList<>();

        public void add(FactJson factJson) {
            facts.add(factJson);
        }
    }
}
