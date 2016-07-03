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
	@Autowired
	protected XmlUtil xmlUtilService;
	final String DocumentDocnoColumn = "DOCNO";

	public String getOracleInValues(JSONArray resultSetToJson) {
		JSONObject jsonDocument;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < resultSetToJson.length(); i++) {
			jsonDocument = resultSetToJson.getJSONObject(i);

			String documentDOCNO = jsonDocument.getString(DocumentDocnoColumn);
			sb.append(",'").append(documentDOCNO).append("'");
		}
		String nos = sb.substring(1);
		return nos;
	}

	public Map<String, JSONArray> resultTOMap(ResultSet rs) throws JSONException, SQLException {
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

				if (columnName.equalsIgnoreCase(DocumentDocnoColumn)) {
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

		Map<String, JSONArray> docNoMapCompany = convertMultiMap(map);
		return docNoMapCompany;
	}

	public Map<String, JSONArray> resultTOMapLinks(ResultSet rs) throws SQLException {
		HashMultimap<String, JSONObject> map = HashMultimap.create();

		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		// travel each row in ResultSet
		while (rs.next()) {
			JSONObject jsonObj = new JSONObject();
			// each column
			String docNo = null;
			for (int i = 1; i <= columnCount; i++) {

				String columnName = metaData.getColumnLabel(i);
				String value = rs.getString(columnName);

				if (columnName.equalsIgnoreCase(DocumentDocnoColumn)) {
					docNo = value;
					continue;
				}
				if (i == 1) {
					final String DslTableSrcDb = "LINK_SRC_DB";
					jsonObj.put(DslTableSrcDb, value);
				} else if (i == 2) {
					final String MolTableSrcDb = "MOLTABLE_SRC_DB";
					jsonObj.put(MolTableSrcDb, value);
				} else if (StringUtils.isBlank(value)) // JSON object has no
														// null value
					jsonObj.put(columnName, "");
				else
					jsonObj.put(columnName, value);
			}
			map.put(docNo, jsonObj);
		}
		Map<String, JSONArray> docNoMapCompany = convertMultiMap(map);
		return docNoMapCompany;
	}

	public JSONArray resultSetToJsonDocument(ResultSet rs)
			throws SQLException, TransformerConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, ParserConfigurationException, SAXException, IOException, DocumentException {
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
						completeXML.add(xmlTagsWithMinus);
						String documentCompletetext = "DOCUMENT_COMPLETETEXT";
						map.put(documentCompletetext, completeXML);

						JSONObject xmlJSONObj = new JSONObject(map);
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

	// one key ,have many values -- multi maps
	private Map<String, JSONArray> convertMultiMap(Multimap<String, JSONObject> multiMap) {
		Map<String, JSONArray> map = new HashMap<>();
		for (String docNo : multiMap.keySet()) {
			JSONArray jsonArray = new JSONArray(multiMap.get(docNo));
			map.put(docNo, jsonArray);
		}
		return map;
	}
}
