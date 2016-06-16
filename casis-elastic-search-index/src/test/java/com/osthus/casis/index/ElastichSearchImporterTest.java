package com.osthus.casis.index;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class ElastichSearchImporterTest {

	private final String esIndex= "bb";
	private final int esPageSize= 2;
	private final int esPageCount= 1;
	private JestClient clinet = ElasticSearchUtil.getEsClinet();
	
	@Test
	public void importFromOralcePartTest() throws Exception {
		
		new ElastichSearchImporter().importFromOralce(esPageSize, esPageCount,esIndex);
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
//		new ElastichSearchImporter().importFromOralce(10, 0, esIndex);
	}
	
	@Test
	public void updateDataFromOracleTest() throws Exception {
		new ElastichSearchImporter().updateDataFromOracle();
	}
	
	
	@Test
	public void importFromOralceArrayTest() throws Exception {
		JSONArray resultSetToJson = new JSONArray();
		resultSetToJson.put("DGL1483041");
		resultSetToJson.put("DGL1510302");
		
		new ElastichSearchImporter().importFromOralce(resultSetToJson, esIndex);
		
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
