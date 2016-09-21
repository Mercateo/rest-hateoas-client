package com.mercateo.rest.hateoas.client.example;

import java.util.Optional;

import com.mercateo.rest.hateoas.client.ClientStarter;
import com.mercateo.rest.hateoas.client.ListResponse;
import com.mercateo.rest.hateoas.client.Response;

public class Example {
	public static void main(String[] args) {
		Response<Object> one = ClientStarter.create("http://localhost:9090", Object.class);
		Optional<ListResponse<OrderProjectionJson>> two = one.prepareNextWithResponse(OrderProjectionJson.class)
				.callListWithRel("orders");

		System.out.println(two.get().get(0).getResponseObject());
		SendBackJson sendBackJson = new SendBackJson("test");

		Optional<Response<Void>> three = two.get().get(1).prepareNextWithResponse(Void.class)
				.withRequestObject(sendBackJson).callWithRel("send-back");

		if (three.isPresent()) {
			System.out.println("sent back");
		} else {
			System.out.println("no send-back available");
		}
	}
}
