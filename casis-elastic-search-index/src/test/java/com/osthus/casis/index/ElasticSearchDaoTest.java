package com.osthus.casis.index;

import java.security.SecureRandom;
import java.math.BigInteger;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osthus.casis.index.ioc.ElasticSearchIocModule;

import de.osthus.ambeth.testutil.AbstractIocTest;
import de.osthus.ambeth.testutil.TestFrameworkModule;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import java.util.UUID;

@TestFrameworkModule(value = ElasticSearchIocModule.class)
public class ElasticSearchDaoTest extends AbstractIocTest {

	JestClient client = null;
	ElasticSearchDao elasticSearchDao= new ElasticSearchDao();

	@Before
	public void before() {
		client = ElasticSearchUtil.getEsClient();
	}

	@Test
	public void bulkIndexTest() throws IOException, InterruptedException {
		// prepare data
		String esIndexName = "junitbulkindextest";
		String uuid1 = UUID.randomUUID().toString();
		
		JSONArray indexData = prepareIndexData(uuid1);
		
		// run
		elasticSearchDao.bulkIndex(indexData, esIndexName);
		 
		
		// check the result
		String objString = getSourceCodeFromEs(esIndexName);
		
		Assert.assertTrue(objString.contains(uuid1));
		
	}

	private JSONArray prepareIndexData(String uuid1) {
		JSONArray indexData = new JSONArray();
		JSONObject jsonObj1 = new JSONObject(
				"{\r\n" + "  \"DOCNO\":\"1\",\r\n" + "  \"22222222222222222\":\"" + uuid1 + "\"\r\n" + "}");

		indexData.put(jsonObj1);
		return indexData;
	}

	@Test
	public void learnIndexTest() throws IOException {
		String esIndexName = "junitlearnindextest";
		
		long startTime = System.currentTimeMillis();
		Bulk.Builder bulkBuilder = new Bulk.Builder();
		for (int i = 0; i < 16; i++) // 16021 -- 3323 ms
		{
			JSONObject docJson = new JSONObject();
			docJson.put(String.valueOf(i), i);
			Index index = new Index.Builder(docJson).index(esIndexName).type("b").build();
			bulkBuilder.addAction(index);
		}
		client.execute(bulkBuilder.build());
		client.shutdownClient();

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);

	}

	@After
	public void after() {
		ElasticSearchUtil.close(client);
	}

	@Test
	public void learnTest() throws IOException{
//		client.execute(new Delete.Builder(null).index("b").build());
	}
	// @Test
	public void mainTest() throws IOException {
	
		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost:9200").multiThreaded(true).build());
		JestClient client = factory.getObject();
	
		// Creating an Index --something is wrong !!
		// client.execute(new CreateIndex.Builder("articles").build());
	
		// Index settings can also be passed during the creation by
		// using a JSON formatted string:
		// String settings = "\"settings\" : {\n" + " \"number_of_shards\" :
		// 5,\n"
		// + " \"number_of_replicas\" : 1\n" + " }\n";
	
		// client.execute(new CreateIndex.Builder("articles")
		// .settings(Settings.builder().loadFromSource(settings).build().getAsMap()).build());
	
		//// using the SettingsBuilder helper class from Elasticsearch:
		// Settings.Builder settingsBuilder = Settings.settingsBuilder();
		// settingsBuilder.put("number_of_shards",5);
		// settingsBuilder.put("number_of_replicas",1);
		// client.execute(new
		//// CreateIndex.Builder("articles").settings(settingsBuilder.build().getAsMap()).build());
	
		// Creating an Index Mapping
		// PutMapping putMapping = new PutMapping.Builder(
		// "my_index",
		// "my_type",
		// "{ \"document\" : { \"properties\" : { \"message\" : {\"type\" :
		// \"string\", \"store\" : \"yes\"} } } }"
		// ).build();
		// client.execute(putMapping);
	
		// The helper class DocumentMapper.Builder from Elasticsearch can also
		// be used to create the mapping source.
		// RootObjectMapper.Builder rootObjectMapperBuilder = new
		// RootObjectMapper.Builder("my_mapping_name").add(
		// new StringFieldMapper.Builder("message").store(true)
		// );
		// DocumentMapper documentMapper = new
		// DocumentMapper.Builder("my_index", null,
		// rootObjectMapperBuilder).build(null, null);
		// String expectedMappingSource =
		// documentMapper.mappingSource().toString();
		// PutMapping putMapping = new PutMapping.Builder(
		// "my_index",
		// "my_type",
		// expectedMappingSource
		// ).build();
		// client.execute(putMapping);
	
		// Indexing Documents
		String source = "{\"user\":\"kimchy\"}";
		//// or creating JSON via ElasticSearch JSONBuilder;
		// String source = jsonBuilder()
		// .startObject()
		// .field("user", "kimchy")
		// .field("postDate", "date")
		// .field("message", "trying out Elastic Search")
		// .endObject().string();
		// as Map;
		// Map<String, String> source = new LinkedHashMap<String,String>();
		// source.put("user", "kimchy");
		// as POJO
		// Article source = new Article();
		// source.setAuthor("John Ronald Reuel Tolkien");
		// source.setContent("The Lord of the Rings is an epic high fantasy
		// novel");
	
		// An example of indexing given source to twitter index with type tweet;
		Index index = new Index.Builder(source).index("twitter").type("tweet").build();
		client.execute(index);
	
		// Index id can be typed explicitly;
	
		// Index index = new
		// Index.Builder(source).index("twitter").type("tweet").id("1").build();
		// client.execute(index);
	
		// Searching Documents
		String query = "{\n" + "    \"query\": {\n" + "        \"filtered\" : {\n" + "            \"query\" : {\n"
				+ "                \"query_string\" : {\n" + "                    \"query\" : \"kimchy\"\n"
				+ "                }\n" + "            },\n" + "            \"filter\" : {\n"
				+ "                \"term\" : { \"user\" : \"kimchy\" }\n" + "            }\n" + "        }\n"
				+ "    }\n" + "}";
		// Search search = new Search.Builder(query)
		// // multiple index or types can be added.
		// .addIndex("twitter")
		// .addType("tweet")
		// .build();
		// System.out.println(query.toString());
	
		// Java/.Net String Escape
		// http://www.freeformatter.com/java-dotnet-escape.html#ad-output
	
		String query1 = "{\r\n  \"query\": {\r\n    \"bool\": {\r\n      \"filter\": {\r\n        \"range\": {\r\n          \"UPD\": {\r\n            \"gte\": 20000120,\r\n            \"lte\": 20100120\r\n          }\r\n        }\r\n      },\r\n      \"must\": [\r\n        {\r\n          \"match\": {\r\n            \"SRC_DB\": \"DGL\"\r\n          }\r\n        }\r\n      ]\r\n    }\r\n  },\r\n  \r\n  \"from\": 0,\r\n  \"size\": 5,\r\n  \r\n  \"sort\": [\r\n    {\r\n      \"UPD\": {\r\n        \"order\": \"asc\"\r\n      }\r\n    }\r\n  ],\r\n  \"aggregations\": {\r\n    \"aggregation_by_date\": {\r\n      \"terms\": {\r\n        \"field\": \"UPD\",\r\n        \"order\": {\r\n          \"_count\": \"desc\"\r\n        },\r\n        \"size\": 3\r\n      }\r\n    }\r\n  }\r\n}";
		// System.out.println(query1.toString());
		Search search1 = new Search.Builder(query1)
				// multiple index or types can be added.
				.addIndex("casis").addType("item").build();
	
		SearchResult result1 = client.execute(search1);
		System.out.println(result1.getJsonString());
	
		String query2 = "{\r\n  \"post_filter\": {\r\n    \"term\": {\r\n      \"PART\": \"8\"\r\n    }\r\n  },\r\n  \r\n  \"from\": 0,\r\n  \"size\": 5, \r\n  \r\n  \"sort\": [\r\n    {\r\n      \"UPD\": {\r\n        \"order\": \"desc\"\r\n      }\r\n    }\r\n  ], \r\n  \r\n  \"aggregations\": {\r\n    \"aggregation_by_date\": {\r\n      \"terms\": {\r\n        \"field\": \"UPD\"\r\n      }\r\n    }\r\n  }\r\n}";
		// System.out.println(query1.toString());
		Search search2 = new Search.Builder(query2)
				// multiple index or types can be added.
				.addIndex("casis").addType("item").build();
	
		SearchResult result2 = client.execute(search2);
		// System.out.println(result2.getJsonString());
	
		// escapeJava("");
	
	}

	private String getSourceCodeFromEs(String esIndexName) throws IOException, JsonProcessingException, InterruptedException {
		Thread.sleep(1000);// wait for index finished
		String queryMatchAll = "{\r\n" + "  \"query\": {\r\n" + "    \"match_all\": {}\r\n" + "  }\r\n" + "}";
		Search search1 = new Search.Builder(queryMatchAll).addIndex(esIndexName).addType("documents").build();
	
		SearchResult result = client.execute(search1);
	
		String allResponse = result.getJsonString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(allResponse);
		JsonNode path = rootNode.path("hits").path("hits");
	
		String objString = null;
		if (path.isArray()) {
			for (final JsonNode objNode : path) {
				objString = objNode.path("_source").toString();
			}
		}
		return objString;
	}

}
