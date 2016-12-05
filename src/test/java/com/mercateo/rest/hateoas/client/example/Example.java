package com.mercateo.rest.hateoas.client.example;

import java.util.Optional;

import com.mercateo.rest.hateoas.client.ClientStarter;
import com.mercateo.rest.hateoas.client.ListResponse;
import com.mercateo.rest.hateoas.client.Response;

import lombok.Value;

public class Example {

	@Value
	public static class IdBean {
		String id;
	}

	public static void main(String[] args) {
		Response<Object> rootResource = new ClientStarter().create("http://localhost:9090", Object.class);
		Optional<ListResponse<OrderProjectionJson>> collectionResource = rootResource
				.prepareNextWithResponse(OrderProjectionJson.class).callListWithRel("orders");

		SendBackJson sendBackJson = new SendBackJson("test");

		Optional<Response<OrderProjectionJson>> secondListElementResource = collectionResource.get().get(1);
		System.out.println(secondListElementResource.get().getResponseObject());
		Optional<Response<Void>> sentBack1 = secondListElementResource.get().prepareNextWithResponse(Void.class)
				.withRequestObject(sendBackJson).callWithRel("send-back");

		if (sentBack1.isPresent()) {
			System.out.println("sent back in list");
		} else {
			System.out.println("no send-back available in list available");
		}

		// demonstrating templated links
		Optional<Response<OrderProjectionJson>> secondElementDirectCall = collectionResource.get()
				.prepareNextWithResponse(OrderProjectionJson.class).withRequestObject(new IdBean("2"))
				.callWithRel("instance");

		System.out.println(secondElementDirectCall.get().getResponseObject());

		Optional<Response<Void>> sentBack2 = secondElementDirectCall.get().prepareNextWithResponse(Void.class)
				.withRequestObject(sendBackJson).callWithRel("send-back");

		if (sentBack2.isPresent()) {
			System.out.println("sent back in template");
		} else {
			System.out.println("no send-back available in template");
		}

	}
}
