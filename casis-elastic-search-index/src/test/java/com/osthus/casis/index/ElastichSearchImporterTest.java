package com.osthus.casis.index;

import java.sql.Connection;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osthus.casis.index.ioc.ElasticSearchIocModule;

import de.osthus.ambeth.ioc.annotation.Autowired;
import de.osthus.ambeth.testutil.AbstractIocTest;
import de.osthus.ambeth.testutil.TestFrameworkModule;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

@TestFrameworkModule(value=ElasticSearchIocModule.class)
public class ElastichSearchImporterTest  extends AbstractIocTest{

	private final String esIndex= "bb";
	private final int esPageSize= 2;
	private final int esPageCount= 1;
	private JestClient clinet = ElasticSearchUtil.getEsClinet();
	
	@Autowired
	private ElastichSearchImporter elastichSearchImporterService ;
	
//	@Autowired
//	protected  IConnectionFactory connectionFactoryService;
	
	@Test
	public void importFromOralcePartTest() throws Exception {
		elastichSearchImporterService.importFromOralce(esPageSize, esPageCount,esIndex);
		
		Thread.sleep(1000);//supend 1 s
		String query1 = "{\r\n" + 
				"  \"size\":1,\r\n" + 
				"  \"query\":\r\n" + 
				"  {\r\n" + 
				"    \"match_all\": {}\r\n" + 
				"  }\r\n" + 
				"}";
		
		Search search1 = new Search.Builder(query1)
				.addIndex(esIndex).addType("documents").build();

		SearchResult result = clinet.execute(search1);
		
//		System.out.println(result.getJsonString());
		String allResponse = result.getJsonString();
//		System.out.println(allResponse);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(allResponse);
		JsonNode path = rootNode.path("hits").path("hits");
		
		
		String objString = null;
		if (path.isArray()) {
		    for (final JsonNode objNode : path) {
		         objString = objNode.path("_source").toString();
		    }
		}
		
		Assert.assertTrue(objString.contains("CHEM_STRUCTURE_DATA"));
		Assert.assertTrue(objString.contains("DOCUMENT"));
		Assert.assertTrue(objString.contains("CASIS_DEVSTATUS"));
		Assert.assertTrue(objString.contains("DOCNO"));
//		Assert.assertTrue(objString.contains("CASIS_COMPOUND"));
//		Assert.assertTrue(objString.contains("CASIS_USE"));
//		Assert.assertTrue(objString.contains("CASIS_COMPANY"));
	}
	
	@Test
	// index the whole database.
	public void importFromOralceTest() throws Exception {
//		elastichSearchImporterService.importFromOralce(1000, 0, esIndex);
	}
	
	@Test
	public void updateDataFromOracleTest() throws Exception {
		System.out.println("top");
//		Thread.sleep(10000000);
//		elastichSearchImporterService.updateDataFromOracle();
	}
	
	
	@Test
	public void importFromOralceArrayTest() throws Exception {
		JSONArray resultSetToJson = new JSONArray();
		resultSetToJson.put("DGL1483041");
		resultSetToJson.put("DGL1510302");
		
		Thread.sleep(1000);//supend 1 s
		String query1 = "{\r\n" + 
				"  \"size\":1,\r\n" + 
				"  \"query\":\r\n" + 
				"  {\r\n" + 
				"    \"match_all\": {}\r\n" + 
				"  }\r\n" + 
				"}";
		
		Search search1 = new Search.Builder(query1)
				.addIndex(esIndex).addType("documents").build();

		SearchResult result = clinet.execute(search1);
		
//		System.out.println(result.getJsonString());
		String allResponse = result.getJsonString();
//		System.out.println(allResponse);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(allResponse);
		JsonNode path = rootNode.path("hits").path("hits");
		
		String objString = null;
		if (path.isArray()) {
		    for (final JsonNode objNode : path) {
		         objString = objNode.path("_source").toString();
		    }
		}
		
		Assert.assertTrue(objString.contains("CHEM_STRUCTURE_DATA"));
		Assert.assertTrue(objString.contains("DOCUMENT"));
		Assert.assertTrue(objString.contains("CASIS_DEVSTATUS"));
		Assert.assertTrue(objString.contains("DOCNO"));
		Assert.assertTrue(objString.contains("CASIS_COMPOUND"));
		Assert.assertTrue(objString.contains("CASIS_USE"));
		Assert.assertTrue(objString.contains("CASIS_COMPANY"));
	}
	
	
	
	@Test
	public void getXXXX2Test() throws Exception {
		
	}
	@Test
	public void getXXXXTest() throws Exception {
		
	}
	
}
