package com.osthus.casis.web;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.osthus.casis.client.ClientJest;

import de.osthus.ambeth.webservice.AbstractServiceREST;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

@Path("/v1")
public class CasisRestController extends AbstractServiceREST
{

	private JestClient client;

	// static
	// {
	// JestClientFactory factory = new JestClientFactory();
	// factory.setHttpClientConfig(new HttpClientConfig.Builder("http://casis.bayer.vmserver:9200").multiThreaded(true).build());
	// JestClient client = factory.getObject();
	// }

	@GET
	@Path("/search")
	// @Produces("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public String getJSON(final String input)
	{

		String query = "{\r\n  \"query\": {\r\n    \"bool\": {\r\n      \"filter\": {\r\n        \"range\": {\r\n          \"UPD\": {\r\n            \"gte\": 20000120,\r\n            \"lte\": 20100120\r\n          }\r\n        }\r\n      },\r\n      \"must\": [\r\n        {\r\n          \"match\": {\r\n            \"SRC_DB\": \"DGL\"\r\n          }\r\n        }\r\n      ]\r\n    }\r\n  },\r\n  \r\n  \"from\": 0,\r\n  \"size\": 1,\r\n  \r\n  \"sort\": [\r\n    {\r\n      \"UPD\": {\r\n        \"order\": \"asc\"\r\n      }\r\n    }\r\n  ],\r\n  \"aggregations\": {\r\n    \"aggregation_by_date\": {\r\n      \"terms\": {\r\n        \"field\": \"UPD\",\r\n        \"order\": {\r\n          \"_count\": \"desc\"\r\n        },\r\n        \"size\": 3\r\n      }\r\n    }\r\n  }\r\n}";
		// query = "{\r\n \"query\":\"names\" \r\n}";
		System.out.println(input);
		return query;
	}

	@POST
	@Path("/search")
	@Consumes("application/json")
	public String sayPlainTextHello(final String input) throws JsonProcessingException, IOException
	{
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder("http://casis.bayer.vmserver:9200").multiThreaded(true).build());
		JestClient client = factory.getObject();
		String requestFromGUI1 = "{" //
				+ "  \"text_search\": {" //
				+ "    \"post_filter\": {" //
				+ "      \"term\": {\r\n        \"PART\": \"8\"\r\n      }\r\n    },\r\n    \"from\": \"0\",\r\n    \"size\": \"2\",\r\n    \"sort\": [\r\n      {\r\n        \"SRC_DB\": {\r\n          \"order\": \"desc\"\r\n        }\r\n      }\r\n    ],\r\n    \"aggregations\": {\r\n      \"aggregation_by_date\": {\r\n        \"terms\": {\r\n          \"field\": \"SRC_DB\"\r\n        }\r\n      }\r\n    }\r\n  }\r\n}";
		// System.out.println(requestFromGUI1);
		// String requestFromGUI1 = input;
		String resposeToGUI1 = new ClientJest().reqToRes(client, input);
		System.out.println(resposeToGUI1);
		System.out.println(input);
		return resposeToGUI1;
	}

}