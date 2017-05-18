package com.mercateo.rest.hateoas.client.example.sse;

import java.util.Optional;

import com.mercateo.rest.hateoas.client.ClientStarter;
import com.mercateo.rest.hateoas.client.OngoingResponse;
import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.SSEObserver;

import lombok.Value;

public class Example {

    @Value
    public static class IdBean {
        String id;
    }

    public static void main(String[] args) throws InterruptedException {
        Response<Object> rootResource = new ClientStarter().create("http://localhost:8080",
                Object.class);
        OngoingResponse<IdBean> sseResponse = rootResource.prepareNextWithResponse(IdBean.class)
                .withRequestObject(new FactRequest(true, "%7B%20%22ns%22%3A%22ab%22%7D"));

        Optional<AutoCloseable> es = sseResponse.subscribe("facts", new SSEObserver<IdBean>() {
            private int count = 0;

            @Override
            public void onSignal(String signal) {
                System.out.println(signal);

            }

            @Override
            public void onEvent(Response<IdBean> response) {
                count++;
                Optional<Response<FactJson>> fact = response.prepareNextWithResponse(FactJson.class)
                        .callWithRel("canonical");
                System.out.println(count + fact.get().getResponseObject().get().toString());

            }

            @Override
            public void onError(String errorCode) {
                System.out.println("error occured" + errorCode);

            }
        }, "new-fact", 1000);
    }
}
