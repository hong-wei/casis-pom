package com.osthus.casis.client;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//https://github.com/searchbox-io/Jest/tree/master/jest

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class ClientJest
{
	public void main(String[] args) throws IOException
	{

		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder("http://casis.bayer.vmserver:9200").multiThreaded(true).build());
		JestClient client = factory.getObject();
	}

	public String reqToRes(JestClient client, String request) throws IOException, JsonProcessingException
	{

		String requestFromGUI1 = request;
		// 1 Extact the Json String to get the core Elastic Search Query
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(requestFromGUI1);
		JsonNode nameNode = rootNode.path("text_search");
		String query = nameNode.toString();

		// 2 call Jest to send the request and get the result
		Search search = new Search.Builder(query).addIndex("casis").addType("item").build();
		SearchResult result = client.execute(search);
		String respose = result.getJsonString();

		// 3 add the "search_results": {} to the top level
		// First way: combine it in the string
		StringBuilder sb = new StringBuilder();
		String resposeToGUI = "{\r\n  \"search_results\":" + respose + "\r\n}";

		// TODO Second way: combine it in Json Object and then change it to String
		return resposeToGUI;
	}
}
