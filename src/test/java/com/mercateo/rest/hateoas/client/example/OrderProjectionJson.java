package com.mercateo.rest.hateoas.client.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NonNull;
import lombok.Value;

/**
 * this class has not all attributes of the OrderJson on the server to
 * demonstrate client side projection
 * 
 * @author joerg_adler
 *
 */
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderProjectionJson {
	private String id;

	private double totalAmount;

	public OrderProjectionJson(@NonNull @JsonProperty("id") String id, @JsonProperty("total") double total) {
		super();
		this.id = id;
		this.totalAmount = total;
	}
}
