package com.osthus.casis.index;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.osthus.casis.index.ioc.ElasticSearchIocModule;

import de.osthus.ambeth.ioc.annotation.Autowired;
import de.osthus.ambeth.testutil.AbstractIocTest;
import de.osthus.ambeth.testutil.TestModule;

@TestModule(value = ElasticSearchIocModule.class)
public class JdbcDaoTest extends AbstractIocTest {

	// test parameters
	String casisIndex = "junitjdbcdaotest";
	String tableName = "CASIS_DOCUMENT_RUNS";
	int size = 1000;

	private Connection conn;

	@Autowired
	protected JdbcDao jdbcDaoService;

	@Before
	public void before() {
		conn = DBManager.getConn();
	}

	@Test
	public void getDocumentLengthTest() throws Exception {
		// prepare data
		tableName = "CASIS_DOCUMENT";

		String sqlCheckLength = "select count(DOCNO) from $tableName";
		String query = sqlCheckLength.replace("$tableName", tableName);
		long lengthTest = 0;
		try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rset = stmt.executeQuery();) {
			rset.next();
			lengthTest = rset.getLong(1);
		}

		// run function
		long length = jdbcDaoService.getDocumentLength(conn, sqlCheckLength, tableName);

		// check result
		Assert.assertEquals(lengthTest, length);
	}

	@Test
	public void getTotalPageCountTest() throws SQLException {
		tableName = "CASIS_DOCUMENT";
		int pageCount = jdbcDaoService.getTotalPageCount(conn, 1000, tableName);
		Assert.assertEquals(1603, pageCount);
	}

	@Test
	public void queryLastTsFromCasisTableAndPreviousRunTableTest() throws SQLException {
		// prepare the data
		Timestamp lastCasisIndexTimestampTest = null;
		Timestamp startTs = null;
		Timestamp endTs = null;
		Timestamp lastIngestedRecordTs = null;
		String loadingProcessActive = null;

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

		String sqlCheckLastRunTable = "SELECT * FROM CASIS2_BG_INGEST_RUNS2 "
				+ "WHERE END_TS = (SELECT MAX(END_TS)AS END_TS FROM CASIS2_BG_INGEST_RUNS2) ";

		try (PreparedStatement ps = conn.prepareStatement(sqlCheckCasisTableIndexTs);
				ResultSet resultSet = ps.executeQuery();) {
			resultSet.next();
			lastCasisIndexTimestampTest = resultSet.getTimestamp("UPDATE_TIMESTAMP");
		}

		try (PreparedStatement ps = conn.prepareStatement(sqlCheckLastRunTable);
				ResultSet resultSet = ps.executeQuery();) {
			resultSet.next();
			startTs = resultSet.getTimestamp("START_TS");
			endTs = resultSet.getTimestamp("END_TS");
			lastIngestedRecordTs = resultSet.getTimestamp("LAST_INGESTED_RECORD_TS");
			loadingProcessActive = resultSet.getString("LOADING_PROCESS_ACTIVE");
		}

		// run the test function
		LastHourState lastHourState = jdbcDaoService.queryLastTsFromCasisTableAndPreviousRunTable(conn);

		// check result
		Assert.assertEquals(lastCasisIndexTimestampTest, lastHourState.getLastCasisIndexTimestamp());
		Assert.assertEquals(startTs, lastHourState.getStartTs());
		Assert.assertEquals(endTs, lastHourState.getEndTs());
		Assert.assertEquals(lastIngestedRecordTs, lastHourState.getLastIngestedRecordTs());
		Assert.assertEquals(loadingProcessActive, lastHourState.getLoadingProcessActive());

	}

	@Test
	public void queryInsertToRunsTableTest() throws SQLException {
		// prepare the data
		java.util.Date date = new java.util.Date();
		long t = date.getTime();

		String loadingProcessActive = null;
		Timestamp lastIngestedRecordTs = null;
		Timestamp startTs = null;

		String loadingProcessActiveTest = "N";
		Timestamp lastIngestedRecordTsTest = new Timestamp(t);
		Timestamp startTsTest = new Timestamp(t);
		LastHourState lastHourUpdate = new LastHourState();
		lastHourUpdate.setLastIngestedRecordTs(lastIngestedRecordTsTest);
		// run function
		jdbcDaoService.queryInsertToRunsTable(conn, startTsTest, lastHourUpdate, loadingProcessActiveTest);

		// check results
		String sqlCheckLastRunTable = "select * from CASIS2_BG_INGEST_RUNS2 where START_TS = ?";

		try (PreparedStatement ps = conn.prepareStatement(sqlCheckLastRunTable);) {
			ps.setTimestamp(1, startTsTest);
			try (ResultSet resultSet = ps.executeQuery();) {
				while (resultSet.next()) {
					startTs = resultSet.getTimestamp("START_TS");
					lastIngestedRecordTs = resultSet.getTimestamp("LAST_INGESTED_RECORD_TS");
					loadingProcessActive = resultSet.getString("LOADING_PROCESS_ACTIVE");
				}
			}
		}

		Assert.assertEquals(startTsTest, startTs);
		Assert.assertEquals(lastIngestedRecordTsTest, lastIngestedRecordTs);
		Assert.assertEquals(loadingProcessActiveTest, loadingProcessActive);

		// delete the database
		String sqlDeleteTestRow = "DELETE CASIS2_BG_INGEST_RUNS2 where START_TS=?";
		try (PreparedStatement ps = conn.prepareStatement(sqlDeleteTestRow);) {
			ps.setTimestamp(1, startTs);
			try (ResultSet resultSet = ps.executeQuery();) {

			}
		}

	}

	@Test
	public void queryDeleteToCasisDocumentRunsTableTest() throws Exception {
		long length = 10;

		jdbcDaoService.queryDeleteToCasisDocumentRunsTable(conn);
		String sqlInsertCasisDocumentTable = " select COUNT(*) from CASIS_DOCUMENT_RUNS ";
		try (PreparedStatement stmt = conn.prepareStatement(sqlInsertCasisDocumentTable);
				ResultSet rset = stmt.executeQuery();) {
			rset.next();
			length = rset.getLong(1);
		}
		Assert.assertEquals(0, length);
	}


	@Test
	public void queryInsertToCasisDocumentRunsTableTest() throws Exception {
//		java.util.Date date = new java.util.Date();
//		long t = date.getTime();
//		Timestamp startTs = new Timestamp(t);
//		Timestamp endTs = new Timestamp(t);
//		Timestamp lastIngestedRecordTs = null;
//		String loadingProcessActive = "Y";
//
//		String sql = "INSERT INTO CASIS2_BG_INGEST_RUNS2 "
//				+ "(START_TS, END_TS, LAST_INGESTED_RECORD_TS, LOADING_PROCESS_ACTIVE) VALUES" + "(?,?,?,?)";
//		PreparedStatement stmt = conn.prepareStatement(sql);
//
//		String sql1 = "SELECT UPDATE_TIMESTAMP FROM CASIS_DOCUMENT where UPDATE_TIMESTAMP in (select max(UPDATE_TIMESTAMP) from CASIS_DOCUMENT)";
//		PreparedStatement stmt1 = conn.prepareStatement(sql1);
//		try (ResultSet rset = stmt1.executeQuery();) {
//			while (rset.next()) {
//				lastIngestedRecordTs = rset.getTimestamp("UPDATE_TIMESTAMP");
//			}
//		}
//
//		stmt.setTimestamp(1, startTs);
//		stmt.setTimestamp(2, endTs);
//		stmt.setTimestamp(3, lastIngestedRecordTs);
//		stmt.setString(4, loadingProcessActive);
//
//		try (ResultSet rset = stmt.executeQuery();) {
//		}

	}

	@After
	public void after() {
		DBManager.closeConn(conn);
	}

	// @Test
	public void learnInsertSqlTest() throws SQLException {
		String sqlInsert = "INSERT INTO CASIS2_BG_INGEST_RUNS2 (START_TS,END_TS,LAST_INGESTED_RECORD_TS,LOADING_PROCESS_ACTIVE) VALUES (?,?,?,?) ";
		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		Timestamp startTs = new Timestamp(t);
		Timestamp endTs = new Timestamp(t);
		Timestamp lastIngestedRecordTs = new Timestamp(t);
		String loadingProcessActive = "Y";

		try (PreparedStatement stmt = conn.prepareStatement(sqlInsert);) {
			stmt.setTimestamp(1, startTs);
			stmt.setTimestamp(2, endTs);
			stmt.setTimestamp(3, lastIngestedRecordTs);
			stmt.setString(4, loadingProcessActive);
			try (ResultSet rset = stmt.executeQuery();) {
			}
		}

	}
}
