package com.mercateo.rest.hateoas.client.example.sse;

import java.util.Optional;

import com.mercateo.rest.hateoas.client.ClientStarter;
import com.mercateo.rest.hateoas.client.OngoingResponse;
import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.SSEObserver;

import lombok.Value;

public class Example {

    static boolean errorOccured = false;

    @Value
    public static class IdBean {
        String id;
    }

    public static void main(String[] args) throws InterruptedException {
        Response<Object> rootResource = new ClientStarter().create("http://localhost:8080",
                Object.class);
        OngoingResponse<IdBean> sseResponse = rootResource.prepareNextWithResponse(IdBean.class)
                .withRequestObject(new FactRequest(true, "%7B%20%22ns%22%3A%22a%22%7D"));

        Optional<AutoCloseable> es = sseResponse.subscribe("facts", new SSEObserver<IdBean>() {

            @Override
            public void onSignal(String signal) {
                System.out.println(signal);

            }

            @Override
            public void onEvent(Response<IdBean> response) {
                System.out.println(response.prepareNextWithResponse(FactJson.class).callWithRel(
                        "canonical").get().getResponseObject().get());

            }

            @Override
            public void onError(String errorCode) {
                errorOccured = true;
                System.out.println("error occured" + errorCode);

            }
        }, "new-fact", 1000);

        while (!errorOccured) {
            Thread.sleep(1000);
        }
    }
}
