package com.mkyong.rest;

import javax.ws.rs.Consumes;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.osthus.ambeth.webservice.AbstractServiceREST;

//  http://localhost:8081/casis-pom/json/product/get
@Path("/json/product")
public class JSONService  extends AbstractServiceREST{

	@GET
	@Path("/get")
	@Produces("application/json")
	public Product getProductInJSON() {

		Product product = new Product();
		product.setName("iPad 3");
		product.setQty(999);
		return product; 

	}

	@GET
	@Path("/get1")
//	@Produces("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public String getJSON(final String input) {

		String query= "{\r\n  \"query\": {\r\n    \"bool\": {\r\n      \"filter\": {\r\n        \"range\": {\r\n          \"UPD\": {\r\n            \"gte\": 20000120,\r\n            \"lte\": 20100120\r\n          }\r\n        }\r\n      },\r\n      \"must\": [\r\n        {\r\n          \"match\": {\r\n            \"SRC_DB\": \"DGL\"\r\n          }\r\n        }\r\n      ]\r\n    }\r\n  },\r\n  \r\n  \"from\": 0,\r\n  \"size\": 1,\r\n  \r\n  \"sort\": [\r\n    {\r\n      \"UPD\": {\r\n        \"order\": \"asc\"\r\n      }\r\n    }\r\n  ],\r\n  \"aggregations\": {\r\n    \"aggregation_by_date\": {\r\n      \"terms\": {\r\n        \"field\": \"UPD\",\r\n        \"order\": {\r\n          \"_count\": \"desc\"\r\n        },\r\n        \"size\": 3\r\n      }\r\n    }\r\n  }\r\n}";
//		query = "{\r\n \"query\":\"names\" \r\n}";
		System.out.println(input);
		return query;
	}

	
	@POST
	@Path("/post")
	@Consumes("application/json")
	public Response createProductInJSON(Product product) {

		String result = "Product created : " + product;
		System.out.println("!!!!!!!!!!!!!!!!!!create it!");
		return Response.status(201).entity(result).build();
		
	}
	
	@POST
	@Path("/post1")
	@Consumes("application/json")
	public String sayPlainTextHello(final String input) {
		System.out.println(input);
		return input;
	}
	
}