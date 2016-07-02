package com.osthus.casis.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
	@Autowired
	private JsonUtil jsonUtil;

	public long checkOralceUpdates(Connection conn, String sqlCheckUpdate) throws SQLException {

		try (PreparedStatement stmt = conn.prepareStatement(sqlCheckUpdate);
				ResultSet rset = stmt.executeQuery();
				ResultSet countQuery = conn.createStatement()
						.executeQuery("select count(DOCNO) from CASIS2_BG_INGEST_RUNS");) {
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

	public long getDocumentLength(Connection conn, String sqlCheckLength, String tableName) throws SQLException {
		long length = 0;
		sqlCheckLength = sqlCheckLength.replace("$tableName", tableName);
		try (PreparedStatement stmt = conn.prepareStatement(sqlCheckLength); ResultSet rset = stmt.executeQuery();) {
			while (rset.next()) {
				length = rset.getLong(1);
			}
		}
		return length;
	}

	public int getTotalPageCount(Connection conn, int size, String tableName) throws SQLException {
		String sqlCheckLength = "select count(DOCNO) from $tableName";
		sqlCheckLength = sqlCheckLength.replace("$tableName", tableName);
		long count = getDocumentLength(conn, sqlCheckLength, tableName);
		int pageCount = (int) Math.ceil((count * 1.0) / size); // 1603
		return pageCount;
	}

	private Timestamp queryToTimestamp(Connection conn, String sqlCheckCasisTableIndexTs) throws SQLException {
		Timestamp lastCasisIndexTimestamp = null;
		try (PreparedStatement ps = conn.prepareStatement(sqlCheckCasisTableIndexTs);
				ResultSet resultSet = ps.executeQuery();) {
			while (resultSet.next()) {
				lastCasisIndexTimestamp = resultSet.getTimestamp("UPDATE_TIMESTAMP");
			}
		}
		return lastCasisIndexTimestamp;
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

	public JSONArray operateByPage(Connection conn, int page, int size, String casisIndex, String tableName)
			throws SQLException, JsonGenerationException, JsonMappingException, JSONException, IOException,
			TransformerException, DocumentException, ParserConfigurationException, SAXException {
		// prepare statement
		int offset = (page + 1) * size;
		int skip = size * page;
		String sql = "select DOCNO,SRC_DB,PART,UPD,DOCUMENT,DATEINSERTED from(select a.*,rownum rn from (select * from $tableName ORDER BY ID) a where rownum <= ?) where rn > ?";
		sql = sql.replace("$tableName", tableName);
		JSONArray resultSetToJson = null;

		try (PreparedStatement stmt = conn.prepareStatement(sql);) {
			stmt.setInt(1, offset);
			stmt.setInt(2, skip);
			try (ResultSet rset = stmt.executeQuery();) {
				resultSetToJson = jsonUtil.resultSetToJsonDocument(rset);
				String nos = jsonUtil.getOracleInValues(resultSetToJson);
				addOtherTables(conn, nos, resultSetToJson);
			}
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

		if (lastHourAllTableState.length() == 0 && casis2bIngestRunsState.length() != 0) {
			triggerUpdateIndexFlag = true;
		}

		// lastHourState.setTriggerUpdateIndexFlag(triggerUpdateIndexFlag);
		// lastHourState.setCasis2bIngestRunsState(casis2bIngestRunsState);
		// lastHourState.setLastHourAllTableState(lastHourAllTableState);

		return lastHourState;
	}

	public void deleteAllTables(Connection conn) throws SQLException {
		String sqlDelete = "truncate table CASIS2_BG_INGEST_RUNS";
		try (PreparedStatement stmt = conn.prepareStatement(sqlDelete); ResultSet rset = stmt.executeQuery();) {
		}
	}

	public LastHourState queryLastTsFromCasisTableAndPreviousRunTable(Connection conn) throws SQLException {
		// test sql is in
		// casis-elastic-search-index/src/test/resources/2016-06-28OracleSqlTest
		String sqlCheckCasisTableIndexTs = "SELECT MAX(UPDATE_TIMESTAMP) as UPDATE_TIMESTAMP  FROM              "
				+ "(                                                                     "
				+ "SELECT MAX(UPDATE_TIMESTAMP)as UPDATE_TIMESTAMP FROM CASIS_DOCUMENT   "
				+ "UNION                                                                 "
				+ "SELECT MAX(UPDATE_TIMESTAMP)as UPDATE_TIMESTAMP FROM CASIS_COMPANY    "
				+ "UNION                                                                 "
				+ "SELECT MAX(UPDATE_TIMESTAMP)as UPDATE_TIMESTAMP FROM CASIS_COMPOUND   "
				+ "UNION                                                                 "
				+ "SELECT MAX(UPDATE_TIMESTAMP) as UPDATE_TIMESTAMP FROM CASIS_USE       "
				+ "UNION                                                                 "
				+ "SELECT MAX(UPDATE_TIMESTAMP)as UPDATE_TIMESTAMP FROM CASIS_DEVSTATUS  "
				+ "UNION                                                                 "
				+ "SELECT MAX(UPDATE_TIMESTAMP)as UPDATE_TIMESTAMP FROM DOC_STRUC_LINK   " + ")";

		String sqlCheckLastRunTable = "SELECT * FROM CASIS2_BG_INGEST_RUNS2 " + 
				"WHERE END_TS = (SELECT MAX(END_TS)AS END_TS FROM CASIS2_BG_INGEST_RUNS2) ";

		LastHourState lastHourState = queryToLastHourState(conn, sqlCheckLastRunTable);

		Timestamp lastCasisIndexTimestamp = null;

		lastCasisIndexTimestamp = queryToTimestamp(conn, sqlCheckCasisTableIndexTs);

		lastHourState.setLastCasisIndexTimestamp(lastCasisIndexTimestamp);

		return lastHourState;
	}

	private LastHourState queryToLastHourState(Connection conn, String sqlCheckLastRunTable) throws SQLException {
		LastHourState lastHourState = new LastHourState();

		try (PreparedStatement ps = conn.prepareStatement(sqlCheckLastRunTable);
				ResultSet resultSet = ps.executeQuery();) {
			while (resultSet.next()) {
				lastHourState.setStartTs(resultSet.getTimestamp("START_TS"));
				lastHourState.setEndTs(resultSet.getTimestamp("END_TS"));
				lastHourState.setLastIngestedRecordTs(resultSet.getTimestamp("LAST_INGESTED_RECORD_TS"));
				lastHourState.setLoadingProcessActive(resultSet.getString("LOADING_PROCESS_ACTIVE"));
			}
		}
		return lastHourState;
	}

	public void queryInsertToRunsTable(Connection conn, Timestamp startTs, LastHourState lastHourUpdate,
			String loadingProcessActive) throws SQLException {
		String sqlInsert = "INSERT INTO CASIS2_BG_INGEST_RUNS2 (START_TS,END_TS,LAST_INGESTED_RECORD_TS,LOADING_PROCESS_ACTIVE) VALUES (?,?,?,?) ";
		Timestamp lastIngestedRecordTs = lastHourUpdate.getLastIngestedRecordTs();

		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		Timestamp endTs = new Timestamp(t);

		try (PreparedStatement stmt = conn.prepareStatement(sqlInsert);) {
			stmt.setTimestamp(1, startTs);
			stmt.setTimestamp(2, endTs);
			stmt.setTimestamp(3, lastIngestedRecordTs);
			stmt.setString(4, loadingProcessActive);
			try (ResultSet rset = stmt.executeQuery();) {
			}
		}
	}

	public void queryInsertToCasisDocumentRunsTable(Connection conn, LastHourState lastHourUpdate) throws SQLException {
		String sqlInsertCasisDocumentTable = "INSERT INTO CASIS_DOCUMENT_RUNS   "
				+ "SELECT DOCNO ,SRC_DB, PART ,UPD, DOCUMENT, DATEINSERTED  FROM CASIS_DOCUMENT  "
				+ "WHERE UPDATE_TIMESTAMP BETWEEN ? and ?";
		try (PreparedStatement stmt = conn.prepareStatement(sqlInsertCasisDocumentTable);) {
			stmt.setTimestamp(1, lastHourUpdate.getLastIngestedRecordTs());
			stmt.setTimestamp(2, lastHourUpdate.getLastCasisIndexTimestamp());
			try (ResultSet rset = stmt.executeQuery();) {
			}
		}
	}

	public void queryDeleteToCasisDocumentRunsTable(Connection conn) throws SQLException {
		String sqlInsertCasisDocumentTable = "truncate table CASIS_DOCUMENT_RUNS";
		try (PreparedStatement stmt = conn.prepareStatement(sqlInsertCasisDocumentTable);
				ResultSet rset = stmt.executeQuery();) {

		}

	}

}
