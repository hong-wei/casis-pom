package com.osthus.casis.index;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.google.common.collect.Multimap;

import de.osthus.ambeth.ioc.annotation.Autowired;

public class JdbcDao {
	// TODO 0 refactory --4 I don't know when the connm ,stmt,rset close.
	@Autowired
	private JsonUtil jsonUtil;

	public long checkOralceUpdates(Connection conn, String sqlCheckUpdate) throws SQLException  {

		try (PreparedStatement stmt = conn.prepareStatement(sqlCheckUpdate); ResultSet rset = stmt.executeQuery();) {
			ResultSet countQuery = conn.createStatement()
					.executeQuery("select count(DOCNO) from CASIS2_BG_INGEST_RUNS");
			countQuery.next();
			long length = countQuery.getLong(1);
			return length;
		}
	}

	public JSONArray readJsonArray(Connection conn, String sqlRead) throws SQLException {
		JSONArray resultSetToJson = new JSONArray();

		try (PreparedStatement stmt = conn.prepareStatement(sqlRead); ResultSet rset = stmt.executeQuery();) {
			while (rset.next()) {
				resultSetToJson.put(rset.getString("DOCNO"));
			}
		}
		return resultSetToJson;
	}



	public long getDocumentLength(Connection conn, String sqlCheckLength) throws SQLException {
		ResultSet countQuery = conn.createStatement().executeQuery("select count(DOCNO) from CASIS_DOCUMENT");
		countQuery.next();
		long length = countQuery.getLong(1);
		return length;
	}

	public int getTotalPageCount(Connection conn, int size) throws SQLException {
		String sqlCheckLength = "select count(DOCNO) from CASIS_DOCUMENT";
		long count = getDocumentLength(conn, sqlCheckLength);

		System.out.println(count);
		int pageCount = (int) Math.ceil((count * 1.0) / size); // 1603
		return pageCount;
	}

	public JSONArray queryToJsonArray(Connection conn, String sqlDocuments)
			throws SQLException, JsonGenerationException, JsonMappingException, IOException, TransformerException,
			DocumentException, JSONException, ParserConfigurationException, SAXException {
		JSONArray resultSetToJson;
		try (PreparedStatement ps = conn.prepareStatement(sqlDocuments); ResultSet resultSet = ps.executeQuery();) {
			resultSetToJson = jsonUtil.resultSetToJsonDocument(resultSet);
		}
		return resultSetToJson;
	}

	public void queryAddTableToJsonarray(Connection conn, JSONArray resultSetToJson, String sql, String key)
			throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet resultSet = ps.executeQuery();) {
			Multimap<String, JSONObject> multiMap = null;
			if (key.equalsIgnoreCase("CHEM_STRUCTURE_DATA"))
				multiMap = jsonUtil.resultTOMapLinks(resultSet);
			else
				multiMap = jsonUtil.resultTOMap(resultSet);

			Map<String, JSONArray> docNoMapCompany = jsonUtil.convertMultiMap(multiMap);
			for (Object obj : resultSetToJson) {
				JSONObject docJson = (JSONObject) obj;
				JSONArray jsonArray = docNoMapCompany.get(docJson.get("DOCNO"));
				docJson.put(key, jsonArray);
			}
		}
	}

	public JSONArray operateByPage(Connection conn, int page, int size, String casisIndex)
			throws SQLException, FileNotFoundException, IOException, JSONException, TransformerException,
			DocumentException, ParserConfigurationException, SAXException {
		// prepare statement
		int offset = (page + 1) * size;
		int skip = size * page;
		String sql = "select DOCNO,SRC_DB,PART,UPD,DOCUMENT,DATEINSERTED from(select a.*,rownum rn from (select * from CASIS_DOCUMENT) a where rownum <= ?) where rn > ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, offset);
		stmt.setInt(2, skip);
		JSONArray resultSetToJson = null;

		try (ResultSet rset = stmt.executeQuery();) {
			try {
				resultSetToJson = jsonUtil.resultSetToJsonDocument(rset);
			} finally {
				try {
					rset.close();
				} catch (Exception ignore) {
				}
			}

			String nos = jsonUtil.getOracleInValues(resultSetToJson);
			addOtherTables(conn, nos, resultSetToJson);
		}
		return resultSetToJson;

	}

	public void addOtherTables(Connection conn, String nos, JSONArray resultSetToJson) throws SQLException {

		String sqlCompany = "select src_db,co, COUNTRY,STATUS, DOCNO from CASIS_COMPANY where DOCNO in(" + nos + ")";
		queryAddTableToJsonarray(conn, resultSetToJson, sqlCompany, "CASIS_COMPANY");

		// addCompounds
		String sqlCompounds = "select src_db,cn, DOCNO from CASIS_COMPOUND where DOCNO in(" + nos + ")";
		queryAddTableToJsonarray(conn, resultSetToJson, sqlCompounds, "CASIS_COMPOUND");

		// addDevstatus
		String sqlDevstatus = "select src_db,DSTA, COUNTRY,DOCNO from CASIS_DEVSTATUS where DOCNO in(" + nos + ")";
		queryAddTableToJsonarray(conn, resultSetToJson, sqlDevstatus, "CASIS_DEVSTATUS");

		// addCASIS_USE
		String sqlCASIS_USE = "select src_db,THE_USE, CODE,SOURCE, DOCNO from CASIS_USE where DOCNO in(" + nos + ")";
		queryAddTableToJsonarray(conn, resultSetToJson, sqlCASIS_USE, "CASIS_USE");

		// addLinks and new table
		String sqlDslCrm = "SELECT dsl.src_db,CRM.SRC_DB, dsl.MDNUMBER,dsl.DOCNO ,CRM.CDBREGNO,CRM.MOLWEIGHT,CRM.MOLDATE,CRM.MOLNAME,CRM.MOLFORMULA,dsl.CASNO "
				+ "from DOC_STRUC_LINK dsl " + "LEFT OUTER JOIN CASIS_RCG_MOLTABLE CRM "
				+ "ON CRM.MDNUMBER = dsl.MDNUMBER " + "where DOCNO in(" + nos + ")";
		queryAddTableToJsonarray(conn, resultSetToJson, sqlDslCrm, "CHEM_STRUCTURE_DATA");

	}

	public LastHourState checkLastHourUpdate(Connection conn) {
		LastHourState lastHourState = new LastHourState();
		String sqlCheckUpdate = "INSERT INTO CASIS2_BG_INGEST_RUNS          "
				+ "(DOCNO)                                    " + "select temp.DOCNO from(                    "
				+ "SELECT DOCNO  FROM CASIS_DOCUMENT          " + "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24)   "
				+ "union all                                  " + "SELECT DOCNO  FROM CASIS_COMPANY           "
				+ "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24)   " + "union all                                  "
				+ "SELECT DOCNO  FROM CASIS_COMPOUND          " + "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24)   "
				+ "union all                                  " + "SELECT DOCNO  FROM CASIS_DEVSTATUS         "
				+ "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24)   " + "union all                                  "
				+ "SELECT DOCNO  FROM CASIS_USE               " + "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24)   "
				+ "union all                                  " + "SELECT DOCNO  FROM DOC_STRUC_LINK          "
				+ "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24)   " + ") TEMP                                     "
				+ "WHERE NOT EXISTS ( SELECT DOCNO FROM CASIS2_BG_INGEST_RUNS WHERE CASIS2_BG_INGEST_RUNS.DOCNO=TEMP.DOCNO)";
		String sqlReadCBIR = "SELECT DOCNO  FROM CASIS2_BG_INGEST_RUNS";
		String sqlReadLastHour = "select DOCNO from(                    "
				+ "SELECT DOCNO  FROM CASIS_DOCUMENT          " + "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24)   "
				+ "union all                                  " + "SELECT DOCNO  FROM CASIS_COMPANY           "
				+ "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24)   " + "union all                                  "
				+ "SELECT DOCNO  FROM CASIS_COMPOUND          " + "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24)   "
				+ "union all                                  " + "SELECT DOCNO  FROM CASIS_DEVSTATUS         "
				+ "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24)   " + "union all                                  "
				+ "SELECT DOCNO  FROM CASIS_USE               " + "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24)   "
				+ "union all                                  " + "SELECT DOCNO  FROM DOC_STRUC_LINK          "
				+ "WHERE UPDATE_TIMESTAMP >= (SYSDATE-1/24))   ";

		JSONArray casis2bIngestRunsState = new JSONArray();
		JSONArray lastHourAllTableState = new JSONArray();

		try {
			checkOralceUpdates(conn, sqlCheckUpdate);
			casis2bIngestRunsState = readJsonArray(conn, sqlReadCBIR);
			lastHourAllTableState = readJsonArray(conn, sqlReadLastHour);
		} catch (Exception e) {
			// TODO log into the log system
			e.printStackTrace();
		}
		boolean triggerUpdateIndexFlag = false;

		if (lastHourAllTableState.length() == 0 && casis2bIngestRunsState.length() != 0){
			triggerUpdateIndexFlag = true;
		}

		lastHourState.setTriggerUpdateIndexFlag(triggerUpdateIndexFlag);
		lastHourState.setCasis2bIngestRunsState(casis2bIngestRunsState);
		lastHourState.setLastHourAllTableState(lastHourAllTableState);

		return lastHourState;
	}
	
	public void deleteAllTables(Connection conn) throws SQLException {
		String sqlDelete = "truncate table CASIS2_BG_INGEST_RUNS";
		try (PreparedStatement stmt = conn.prepareStatement(sqlDelete); ResultSet rset = stmt.executeQuery();) {
		}

	}
	
	public void deleteAllTables1(Connection conn) {
		// TODO Auto-generated method stub
		
	}

}
