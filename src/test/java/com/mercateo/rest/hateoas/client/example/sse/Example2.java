package com.mercateo.rest.hateoas.client.example.sse;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.mercateo.rest.hateoas.client.ClientStarter;
import com.mercateo.rest.hateoas.client.OngoingResponse;
import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.SSEObserver;

import lombok.Value;

public class Example2 {

    @Value
    public static class IdBean {
        String id;
    }

    public static void main(String[] args) throws Exception {
        Response<Object> rootResource = new ClientStarter().create("http://localhost:8080",
                Object.class);
        OngoingResponse<FactJson> sseResponse = rootResource.prepareNextWithResponse(FactJson.class)
                .withRequestObject(new FactRequest(true, "%7B%20%22ns%22%3A%22ab%22%7D"));
        Stopwatch stopwatch = Stopwatch.createStarted();
        Optional<AutoCloseable> es = sseResponse.subscribe("full-facts",
                new SSEObserver<FactJson>() {
                    private int count = 0;

                    @Override
                    public void onSignal(String signal) {
                        System.out.println(signal + " after " + stopwatch.elapsed(TimeUnit.SECONDS)
                                + " and " + count + " objects received");

                    }

                    @Override
                    public void onEvent(Response<FactJson> response) {
                        count++;
                        System.out.println(count + ":" + response.getResponseObject().get()
                                .toString());

                    }

                    @Override
                    public void onError(String errorCode) {
                        System.out.println("error occured" + errorCode);

                    }
                }, "new-fact", 1000);
    }
}
