package com.osthus.casis.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.osthus.ambeth.collections.HashMap;

//https://github.com/searchbox-io/Jest/tree/master/jest

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class ClientJestTest
{
	public static void main(String[] args) throws IOException
	{

		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder("http://casis.bayer.vmserver:9200").multiThreaded(true).build());
		JestClient client = factory.getObject();

		// solution 1
		String requestFromGUI1 = "{" //
				+ "  \"text_search\": {" //
				+ "    \"post_filter\": {" //
				+ "      \"term\": {\r\n        \"PART\": \"8\"\r\n      }\r\n    },\r\n    \"from\": \"0\",\r\n    \"size\": \"2\",\r\n    \"sort\": [\r\n      {\r\n        \"SRC_DB\": {\r\n          \"order\": \"desc\"\r\n        }\r\n      }\r\n    ],\r\n    \"aggregations\": {\r\n      \"aggregation_by_date\": {\r\n        \"terms\": {\r\n          \"field\": \"SRC_DB\"\r\n        }\r\n      }\r\n    }\r\n  }\r\n}";
		System.out.println(requestFromGUI1);
		String resposeToGUI1 = reqToRes(client, requestFromGUI1);
		System.out.println(resposeToGUI1);

		// solution 2
		// String requestFromGUI2 = "{\r\n \"text_search\": {\r\n \"query\": {\r\n \"bool\": {\r\n \"filter\": {\r\n \"range\": {\r\n \"UPD\": {\r\n \"gte\":
		// 20000120,\r\n \"lte\": 20100120\r\n }\r\n }\r\n },\r\n \"must\": [\r\n {\r\n \"match\": {\r\n \"SRC_DB\": \"DGL\"\r\n }\r\n }\r\n ]\r\n }\r\n },\r\n
		// \r\n \"from\": 0,\r\n \"size\": 1,\r\n \r\n \"sort\": [\r\n {\r\n \"UPD\": {\r\n \"order\": \"asc\"\r\n }\r\n }\r\n ],\r\n \"aggregations\": {\r\n
		// \"aggregation_by_date\": {\r\n \"terms\": {\r\n \"field\": \"UPD\",\r\n \"order\": {\r\n \"_count\": \"desc\"\r\n },\r\n \"size\": 3\r\n }\r\n }\r\n
		// }\r\n} \r\n}";
		// System.out.println(requestFromGUI2);
		// String resposeToGUI2 = reqToRes(client, requestFromGUI2);
		// System.out.println(resposeToGUI2);

		// TODO save all the data in ES to small case .
		// TODO modify part of the Json
		// String part = "{\"part\":\"8\"}";
		// System.out.println(part);
		// Part partObject = mapper.readValue(part, Part.class);
		//
		// ObjectNode node1 = (ObjectNode) blablas;
		// System.out.println(node1.get("PART"));
		// node1.put("PART", "1");
		//
		// System.out.println(node1.get("PART"));
		// JsonNode nameNode1 = rootNode.path("text_search").path("post_filter").path("term").path("PART");
		// String part = nameNode1.toString();
		// System.out.println(part);

	}

	@Test
	public void indexTest() throws IOException
	{

		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder("http://casis.bayer.vmserver:9200").multiThreaded(true).build());
		JestClient client = factory.getObject();

		// index build
		// String indexQuery =
		// "{\r\n\"DOCNO\":\"DGL1342926\",\r\n\"SRC_DB\":\"DGL\",\r\n\"PART\":8,\r\n\"UPD\":20070828,\r\n\"DATEINSERTED\":\"03-MAR-16\",\r\n\r\n\"CASIS_COMPANY\"
		// :[\r\n {\"SRC_DB\":\"DGL\",\"CO\":\"Country Life\",\"COUNTRY\":\"\",\"STATUS\" :\"MANUFACTURER\"},\r\n {\"SRC_DB\":\"DGL\",\"CO\":\"COUNTRY
		// LIFE\",\"COUNTRY\":\"\",\"STATUS\" :\"CORPORATION\"}], \r\n\r\n\"CASIS_COMPOUND\" :[ \r\n {\"SRC_DB\":\"DGL\",\"CN\":\"C.L CO-Q10\"}], \r\n
		// \r\n\"CASIS_DEVSTATUS\":[\r\n {\"SRC_DB\":\"DGL\",\"DSTA\":\"Launched 20070201\",\"COUNTRY\":\"TR\"}], \r\n \r\n\"CASIS_USE\" :[\r\n
		// {\"SRC_DB\":\"DGL\",\"THE_USE\":\"Aids cardiovascular health, antioxidant.\",\"CODE\":\"\",\"SOURCE\" :\"IND\"},\r\n
		// {\"SRC_DB\":\"DGL\",\"THE_USE\":\"All Other Cardiac Preparations\",\"CODE\":\"C1X\",\"SOURCE\" :\"ACT\"}], \r\n \r\n\"DOC_STRUC_LINK\" :[\r\n
		// {\"SRC_DB\":\"DGL\",\"CASNO\":\"\",\"MDNUMBER\":\"MD000001\"}], \r\n
		// \r\n\"Document\":{\"LaunchDetails\":{\"LaunchDateComment\":\"\",\"Ingredients\":{\"Ingredient\":\"ubidecarenone\"},\"NewChemicalEntity\":\"\",\"CASIS-DSTA\":{\"LaunchDate\":{\"content\":\"01
		// Feb 2007\",\"CCYYMM\":20070201},\"CASIS-NORMALIZED-DSTA\":\"TR: Launched 20070201\",\"Country\":\"Turkey\"},\"CASIS-CO\":{\"Manufacturer\":\"Country
		// Life\",\"Corporation\":\"COUNTRY LIFE\",\"CASIS-NORMALIZED-CO\":[\"Manufacturer: Country Life\",\"COUNTRY
		// LIFE\"]},\"CASIS-RN\":{\"CASInfo\":{\"CASItem\":\"\"}},\"CASIS-CN\":{\"BrandName\":\"C-L
		// CO-Q10\"},\"CASIS-TX\":{\"Unbranded\":\"Yes\",\"Biotech\":\"No\"},\"CASIS-USE\":{\"CASIS-ACT\":{\"Class\":{\"ClassDescription\":\"All Other Cardiac
		// Preparations\",\"ClassCode\":\"C1X\"}},\"CASIS-IND\":{\"Indication\":\"Aids cardiovascular health,
		// antioxidant-\"}}},\"LaunchStatus\":{\"RecordStatus\":\"\"},\"CASIS-UPD\":{\"PublicationDate\":{\"content\":\"28 Aug
		// 2007\",\"CCYYMMDD\":20070828}},\"CASIS-DOCNO\":\"DGL1342926\",\"PackInfo\":{\"PriceInfo\":{\"Price\":\"\"},\"NumberOfIngredients\":1,\"ExcipientInfo\":{\"Excipient\":\"\"},\"DoseFormInfo\":{\"DoseForm\":\"caps\"},\"CASIS-TX\":{\"CompositionInfo\":{\"Composition\":\"caps:
		// ubidecarenone, 30 MIU\"}}},\"CASIS-MDNUMBER\":\"MD000001\"} \r\n}";
		// Index index = new Index.Builder(indexQuery).index("casis").type("item").build();
		// DocumentResult execute = client.execute(index);
		// System.out.println(execute.getJsonString());
		long startTime = System.currentTimeMillis();
		indexBulk("today1", client);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);

	}

	// http://www.cnblogs.com/huangfox/p/3542858.html
	// @Test
	public void indexBulk(String indexName, JestClient client)
	{
		try
		{
			Bulk.Builder bulkBuilder = new Bulk.Builder();
			for (int i = 0; i < 16; i++) // 16021 -- 3323 ms
			{

				User user = new User();
				user.setId(new Long(i));
				user.setName("huang fox " + i);
				user.setAge(i % 100);
				Index index = new Index.Builder(user).index(indexName).type(indexName).build();
				bulkBuilder.addAction(index);
			}
			client.execute(bulkBuilder.build());
			client.shutdownClient();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void indexBulkTest() throws IOException
	{

		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder("http://casis.bayer.vmserver:9200").multiThreaded(true).build());
		JestClient client = factory.getObject();

		// index build
		String article1 = "tweet1";
		String article2 = "tweet2";
		String index = "twitter";
		String type = "tweet";
		String id = "1";

		Map<String, String> source = new HashMap<String, String>();
		source.put("user", "kimcy");
		Bulk bulk = new Bulk.Builder().defaultIndex("twitter").defaultType("tweet")
				.addAction(Arrays.asList(new Index.Builder(source).build(), new Index.Builder(source).build())).build();

		client.execute(bulk);

	}

	public static String reqToRes(JestClient client, String request) throws IOException, JsonProcessingException
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
