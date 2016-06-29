package com.osthus.casis.index;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.osthus.ambeth.ioc.annotation.Autowired;

public class JsonUtil {
	//TODO fix -1 Unitl only use the static methods
	@Autowired
	protected XmlUtil xmlUtilService;
	
	//TODO 0 refactory --5 mix the following two methods in one, is it right ?
	public String getOralceInvalues2(JSONArray resultSetToJson) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < resultSetToJson.length(); i++) {
			String documentDOCNO = resultSetToJson.getString(i);
			sb.append(",'").append(documentDOCNO).append("'");
		}
		String nos = sb.substring(1);
		return nos;
	}
	
	public String getOracleInValues(JSONArray resultSetToJson) {
		JSONObject jsonDocument;
		StringBuilder sb = new StringBuilder();
		 if (resultSetToJson.getJSONObject(0) instanceof JSONObject) {
		for (int i = 0; i < resultSetToJson.length(); i++) {
			jsonDocument = resultSetToJson.getJSONObject(i);
			String documentDOCNO = jsonDocument.getString("DOCNO");
			sb.append(",'").append(documentDOCNO).append("'");
		}
		 } else {
		 for (int i = 0; i < resultSetToJson.length(); i++) {
		 String documentDOCNO = resultSetToJson.getString(i);
		 sb.append(",'").append(documentDOCNO).append("'");
		 }
		 }
		String nos = sb.substring(1);
		return nos;
	}

	public Multimap<String, JSONObject> resultTOMap(ResultSet rs) throws SQLException {
		HashMultimap<String, JSONObject> map = HashMultimap.create();

		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		// travel each row in ResultSet
		while (rs.next()) {
			JSONObject jsonObj = new JSONObject();
			// each collom
			String docNo = null;
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnLabel(i);
				String value = rs.getString(columnName);

				if (columnName.equalsIgnoreCase("DOCNO")) {
					docNo = value;
					continue;
				}
				if (StringUtils.isBlank(value)) // JSON object has no null value
					jsonObj.put(columnName, "");
				else
					jsonObj.put(columnName, value);
			}
			map.put(docNo, jsonObj);
		}
		return map;
	}

	public Multimap<String, JSONObject> resultTOMapLinks(ResultSet rs) throws SQLException {
		HashMultimap<String, JSONObject> map = HashMultimap.create();

		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		// travel each row in ResultSet
		while (rs.next()) {
			JSONObject jsonObj = new JSONObject();
			// each collom
			String docNo = null;
			for (int i = 1; i <= columnCount; i++) {

				String columnName = metaData.getColumnLabel(i);
				String value = rs.getString(columnName);

				if (columnName.equalsIgnoreCase("DOCNO")) {
					docNo = value;
					continue;
				}
				if (i == 1) { // TODO 1 fix the hard code, it is not useful
					jsonObj.put("LINK_SRC_DB", value);
				} else if (i == 2) {
					jsonObj.put("MOLTABLE_SRC_DB", value);
				} else if (StringUtils.isBlank(value)) // JSON object has no
														// null value
					jsonObj.put(columnName, "");
				else
					jsonObj.put(columnName, value);
			}
			map.put(docNo, jsonObj);
		}
		return map;
	}

	public Map<String, JSONArray> convertMultiMap(Multimap<String, JSONObject> multiMap) {
		Map<String, JSONArray> map = new HashMap<>();
		for (String docNo : multiMap.keySet()) {
			JSONArray jsonArray = new JSONArray(multiMap.get(docNo));
			map.put(docNo, jsonArray);
		}
		return map;
	}

	public JSONArray resultSetToJsonDocument(ResultSet rs) throws SQLException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException, SAXException, IOException, DocumentException
			  {
		JSONArray array = new JSONArray();

		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		// travel each row in ResultSet
		while (rs.next()) {
			JSONObject jsonObj = new JSONObject();

			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnLabel(i);
				if ("DOCUMENT".equalsIgnoreCase(columnName)) {
					// TODO refactory the code here:
					String xmlTagsWithDot = rs.getString(columnName);
					if (xmlTagsWithDot != null) { 
						String xmlTagsWithMinus = xmlUtilService.renameTagsDotToMinus(xmlTagsWithDot);
						HashMap<String, ArrayList<String>> map = xmlUtilService.getXmlKeyValuesPairs(xmlTagsWithMinus);

						ArrayList<String> completeXML = new ArrayList<String>();
						// String valueUnderline1
						// =StringEscapeUtils.escapeXml(valueUnderline);
						completeXML.add(xmlTagsWithMinus);

						map.put("DOCUMENT_COMPLETETEXT", completeXML);

						// TODO 01 test the new functions -- these memery should
						JSONObject xmlJSONObj = new JSONObject(map);

						// System.out.println(xmlJSONObj.toString());
						jsonObj.put(columnName, xmlJSONObj);
					}

				} else {
					String value = rs.getString(columnName);
					jsonObj.put(columnName, value);
				}
			}
			array.put(jsonObj);
		}

		return array;
	}
}
