package com.osthus.casis.index;

import java.security.SecureRandom;
import java.math.BigInteger;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

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

	@Test
	public void bulkIndexTest() throws IOException {

		
		// prepare data
		String uuid1 = UUID.randomUUID().toString();
		String uuid2 = UUID.randomUUID().toString();
		JSONArray indexData = new JSONArray();
		JSONObject jsonObj1 = new JSONObject();

		jsonObj1.put("DOCNO", uuid1);
		indexData.put(jsonObj1);
		JSONObject jsonObj2 = new JSONObject();

		jsonObj2.put("DOCNO", uuid2);
		indexData.put(jsonObj2);
		

		// run
		new ElasticSearchDao().bulkIndex(indexData, "junit-bulkindextest");

//		 // get the result from ES
//		 Thread.sleep(1000);// supend 1 s
//		 String query1 = "{\r\n" + " \"size\":1,\r\n" + " \"query\":\r\n" + "
//		 {\r\n" + " \"match_all\": {}\r\n"
//		 + " }\r\n" + "}";
//		
//		 Search search1 = new
//		 Search.Builder(query1).addIndex(esIndex).addType("documents").build();
//		
//		 SearchResult result = clinet.execute(search1);
//		
//		 // System.out.println(result.getJsonString());
//		 String allResponse = result.getJsonString();
//		 // System.out.println(allResponse);
//		 ObjectMapper mapper = new ObjectMapper();
//		 JsonNode rootNode = mapper.readTree(allResponse);
//		 JsonNode path = rootNode.path("hits").path("hits");
//		
//		 String objString = null;
//		 if (path.isArray()) {
//		 for (final JsonNode objNode : path) {
//		 objString = objNode.path("_source").toString();
//		 }
//		 }
//		
//		
//		 Assert.assertTrue(objString.contains("DOCNO"));
//		 Assert.assertTrue(objString.contains("DOCUMENT"));
//		
//		 if(objString.contains("CHEM_STRUCTURE_DATA")){
//		 Assert.assertTrue(objString.contains("CHEM_STRUCTURE_DATA"));
//		 }
//		 if(objString.contains("CASIS_DEVSTATUS")){
//		 Assert.assertTrue(objString.contains("CASIS_DEVSTATUS"));
//		 }
//		 if(objString.contains("CASIS_COMPOUND")){
//		 Assert.assertTrue(objString.contains("CASIS_COMPOUND"));
//		 }
//		 if(objString.contains("CASIS_USE")){
//		 Assert.assertTrue(objString.contains("CASIS_USE"));
//		 }
//		 if(objString.contains("CASIS_COMPANY")){
//		 Assert.assertTrue(objString.contains("CASIS_COMPANY"));
//		 }

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

	@Test
	public void indexTest() throws IOException {

		// // Construct a new Jest client according to configuration via factory
		// JestClientFactory factory = new JestClientFactory();
		// factory.setHttpClientConfig(new
		// HttpClientConfig.Builder("http://casis.bayer.vmserver:9200").multiThreaded(true).build());
		// JestClient client = factory.getObject();
		//
		// long startTime = System.currentTimeMillis();
		// Bulk.Builder bulkBuilder = new Bulk.Builder();
		// for (int i = 0; i < 16; i++) // 16021 -- 3323 ms
		// {
		//
		// User user = new User();
		// user.setId(new Long(i));
		// user.setName("huang fox " + i);
		// user.setAge(i % 100);
		// Index index = new Index.Builder(user).index("a").type("a").build();
		// bulkBuilder.addAction(index);
		// }
		// client.execute(bulkBuilder.build());
		// client.shutdownClient();
		//
		// long endTime = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		// System.out.println(totalTime);

	}

	@Test
	public void indexTest1() throws IOException {

		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(
				new HttpClientConfig.Builder("http://casis.bayer.vmserver:9200").multiThreaded(true).build());
		JestClient client = factory.getObject();

		long startTime = System.currentTimeMillis();
		Bulk.Builder bulkBuilder = new Bulk.Builder();
		for (int i = 0; i < 16; i++) // 16021 -- 3323 ms
		{

			JSONObject docJson = new JSONObject();
			docJson.put(String.valueOf(i), i);
			Index index = new Index.Builder(docJson).index("b").type("b").build();
			bulkBuilder.addAction(index);
		}
		client.execute(bulkBuilder.build());
		client.shutdownClient();

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime);

	}

	// http://www.cnblogs.com/huangfox/p/3542858.html
	// @Test
	public void indexBulk(String indexName, JestClient client) throws IOException {
		// try
		// {
		// Bulk.Builder bulkBuilder = new Bulk.Builder();
		// for (int i = 0; i < 16; i++) // 16021 -- 3323 ms
		// {
		//
		// User user = new User();
		// user.setId(new Long(i));
		// user.setName("huang fox " + i);
		// user.setAge(i % 100);
		// Index index = new
		// Index.Builder(user).index(indexName).type(indexName).build();
		// bulkBuilder.addAction(index);
		// }
		// client.execute(bulkBuilder.build());
		// client.shutdownClient();
		//// }
		// catch (IOException e)
		// {
		// e.printStackTrace();
		// }
		// }

	}

	@Test
	public void getOraclConnectionTest() throws IOException {

		String complexString = "myowndataisfortesttestthisiaissjjsfjsakfjksajfklsjfskljflskfspeicl";
		JestClient clinet = ElasticSearchUtil.getEsClinet();

		String indexTest = "{ \"test\":\"myowndataisfortesttest\"}";
		Bulk.Builder bulkBuilder = new Bulk.Builder();

		Index index = new Index.Builder(indexTest).index("aa").type("documents").id("myJunitTestID").build();
		bulkBuilder.addAction(index);
		clinet.execute(bulkBuilder.build());

		String query1 = "{\r\n  \"_source\": \"test\", \r\n  \"query\": {\r\n    \"match\": {\r\n      \"test\": \"myowndataisfortesttest\"\r\n    }\r\n  }\r\n}";
		// System.out.println(query1.toString());
		Search search1 = new Search.Builder(query1)
				// multiple index or types can be added.
				.addIndex("aa").addType("documents").build();

		SearchResult result = clinet.execute(search1);

		// System.out.println(result.getJsonString());
		String allResponse = result.getJsonString();
		// System.out.println(allResponse);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(allResponse);
		JsonNode path = rootNode.path("hits").path("hits");
		// System.out.println(path);
		String subResponse = path.toString();

		boolean contains = subResponse.contains("myowndataisfortesttest");
		Assert.assertEquals(true, contains);

		clinet.execute(new Delete.Builder("myJunitTestID").index("aa").type("documents").build());

	}

}
