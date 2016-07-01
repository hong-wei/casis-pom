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
	String casisIndex = "casisvm5";
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
	public void checkOralceUpdatesTest() throws Exception {
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

		jdbcDaoService.checkOralceUpdates(conn, sqlCheckUpdate);

		System.out.println(jdbcDaoService.checkOralceUpdates(conn, sqlCheckUpdate));
	}

	@Test
	public void checkLastHourUpdateTest() throws Exception {

		LastHourState checkLastHourUpdate = jdbcDaoService.checkLastHourUpdate(conn);
		// (resultLastHour.length() == 0 && resultCBIR.length() != 0)
		// JSONArray casis2bIngestRunsState =
		// checkLastHourUpdate.getCasis2bIngestRunsState();
		// JSONArray lastHourAllTableState =
		// checkLastHourUpdate.getLastHourAllTableState();
		// boolean triggerUpdateIndexFlag =
		// checkLastHourUpdate.isTriggerUpdateIndexFlag();

		// boolean state = resultCBIR.length()
		// Assert.assertTrue(!triggerUpdateIndexFlag);

	}

	@Test
	public void readJsonArrayTest() throws Exception {

		String sqlRead = "SELECT DOCNO  FROM CASIS2_BG_INGEST_RUNS";

		JSONArray resultSetToJson = new JSONArray();
		try {
			resultSetToJson = jdbcDaoService.readJsonArray(conn, sqlRead);
		} catch (Exception e) {
			// TODO log into the log system
			e.printStackTrace();
		}
		System.out.println(resultSetToJson.toString());
	}

	@Test
	public void queryToJsonArrayTest() throws Exception {
		String sqlDocuments = "select DOCNO,SRC_DB,PART,UPD,DOCUMENT,DATEINSERTED from CASIS_DOCUMENT where DOCNO in( 'DGL1483041','DGL1483041')";
		JSONArray resultSetToJson = jdbcDaoService.queryToJsonArray(conn, sqlDocuments);
		String expectResult = "\"PART\":\"8\",\"UPD\":\"20130928\",\"DOCUMENT\":{\"Document_CASIS-DOCNO\":[\"DGL1483041\"],\"Document_CASIS-UPD_PublicationDate\":[\"28 Sep 2013\"],\"Document_LaunchDetails_CASIS-CO_CASIS-NORMALIZED-CO\":[\"Manufacturer: CHALVER\",\"CHALVER\"],\"Document_LaunchDetails_CASIS-TX_Biotech\":[\"No\"],\"Document_PackInfo_ExcipientInfo_Excipient\":[\"\"],\"Document_LaunchDetails_CASIS-USE_CASIS-ACT_Class_ClassDescription\":[\"Cold Preparations Without Anti-infectives\"],\"Document_LaunchDetails_CASIS-CN_BrandName\":[\"FLUZETRIN F\"],\"Document_PackInfo_DoseFormInfo_DoseForm\":[\"tabs\",\"drops oral\",\"syrup oral\"],\"Document_PackInfo_CASIS-TX_CompositionInfo_Composition\":[\"tabs: cetirizine hydrochloride, 5 mg; paracetamol base, 500 mg; phenylephrine hydrochloride, 10 mg\",\"syrup oral: cetirizine hydrochloride, 2.5 mg/5 ml; paracetamol base, 325 mg/5 ml; phenylephrine hydrochloride, 5 mg/5 ml\",\"drops oral: cetirizine hydrochloride, 1 mg/1 ml; paracetamol base, 100 mg/1 ml; phenylephrine hydrochloride, 10 mg/1 ml\"],\"Document_LaunchDetails_CASIS-USE_CASIS-ACT_Class_ClassCode\":[\"R5A\"],\"Document_LaunchDetails_LaunchDateComment\":[\"\"],\"Document_PackInfo_PriceInfo_Price\":[\"\"],\"Document_LaunchDetails_CASIS-CO_Manufacturer\":[\"CHALVER\"],\"Document_LaunchDetails_CASIS-DSTA_LaunchDate_CCYYMM\":[\"20110201\"],\"Document_LaunchDetails_CASIS-DSTA_LaunchDate\":[\"01 Feb 2011\"],\"Document_LaunchDetails_CASIS-CO_Corporation\":[\"CHALVER\"],\"Document_LaunchDetails_CASIS-RN_CASInfo_CASItem\":[\"\"],\"Document_LaunchDetails_CASIS-USE_CASIS-IND_Indication\":[\"Common cold, influenza.\"],\"Document_LaunchDetails_CASIS-DSTA_Country\":[\"Ecuador\"],\"Document_LaunchDetails_CASIS-DSTA_CASIS-NORMALIZED-DSTA\":[\"EC: Launched 20110201\"],\"Document_PackInfo_NumberOfIngredients\":[\"3\"],\"Document_LaunchDetails_CASIS-TX_Unbranded\":[\"No\"],\"Document_CASIS-UPD_PublicationDate_CCYYMMDD\":[\"20130928\"],\"Document_LaunchDetails_Ingredients_Ingredient\":[\"cetirizine\",\"paracetamol\",\"phenylephrine\"],\"Document_LaunchStatus_RecordStatus\":[\"\"],\"Document_CASIS-MDNUMBER\":[\"MD000001\"],\"DOCUMENT_COMPLETETEXT\":[\"<Document><CASIS-DOCNO>DGL1483041<\\/CASIS-DOCNO><CASIS-UPD><PublicationDate CCYYMMDD=\\\"20130928\\\">28 Sep 2013<\\/PublicationDate><\\/CASIS-UPD><LaunchDetails><LaunchDateComment/><NewChemicalEntity/><Ingredients><Ingredient>cetirizine<\\/Ingredient><Ingredient>paracetamol<\\/Ingredient><Ingredient>phenylephrine<\\/Ingredient><\\/Ingredients><CASIS-USE><CASIS-ACT><Class><ClassCode>R5A<\\/ClassCode><ClassDescription>Cold Preparations Without Anti-infectives<\\/ClassDescription><\\/Class><\\/CASIS-ACT><CASIS-IND><Indication>Common cold, influenza.<\\/Indication><\\/CASIS-IND><\\/CASIS-USE><CASIS-CN><BrandName>FLUZETRIN F<\\/BrandName><\\/CASIS-CN><CASIS-CO><CASIS-NORMALIZED-CO>Manufacturer: CHALVER<\\/CASIS-NORMALIZED-CO><CASIS-NORMALIZED-CO>CHALVER<\\/CASIS-NORMALIZED-CO><Manufacturer>CHALVER<\\/Manufacturer><Corporation>CHALVER<\\/Corporation><\\/CASIS-CO><CASIS-DSTA><CASIS-NORMALIZED-DSTA>EC: Launched 20110201<\\/CASIS-NORMALIZED-DSTA><Country>Ecuador<\\/Country><LaunchDate CCYYMM=\\\"20110201\\\">01 Feb 2011<\\/LaunchDate><\\/CASIS-DSTA><CASIS-TX><Biotech>No<\\/Biotech><Unbranded>No<\\/Unbranded><\\/CASIS-TX><CASIS-RN><CASInfo><CASItem/><\\/CASInfo><\\/CASIS-RN><\\/LaunchDetails><PackInfo><ExcipientInfo><Excipient/><\\/ExcipientInfo><PriceInfo><Price/><\\/PriceInfo><DoseFormInfo><DoseForm>tabs<\\/DoseForm><DoseForm>drops oral<\\/DoseForm><DoseForm>syrup oral<\\/DoseForm><\\/DoseFormInfo><NumberOfIngredients>3<\\/NumberOfIngredients><CASIS-TX><CompositionInfo><Composition>tabs: cetirizine hydrochloride, 5 mg; paracetamol base, 500 mg; phenylephrine hydrochloride, 10 mg<\\/Composition><Composition>syrup oral: cetirizine hydrochloride, 2.5 mg/5 ml; paracetamol base, 325 mg/5 ml; phenylephrine hydrochloride, 5 mg/5 ml<\\/Composition><Composition>drops oral: cetirizine hydrochloride, 1 mg/1 ml; paracetamol base, 100 mg/1 ml; phenylephrine hydrochloride, 10 mg/1 ml<\\/Composition><\\/CompositionInfo><\\/CASIS-TX><\\/PackInfo><LaunchStatus><RecordStatus/><\\/LaunchStatus><CASIS-MDNUMBER>MD000001<\\/CASIS-MDNUMBER><\\/Document>\"],\"Document_LaunchDetails_NewChemicalEntity\":[\"\"]},\"DOCNO\":\"DGL1483041\",\"DATEINSERTED\":\"2016-03-03 09:25:47\",\"SRC_DB\":\"DGL\"";
		String revicerResult = resultSetToJson.toString();
		System.out.println(revicerResult);
		Assert.assertTrue(revicerResult.contains(expectResult));

	}

	// @Test
	// public void deleteAllTablesTest() throws Exception {
	//
	// String sqlDelete = "truncate table CASIS2_BG_INGEST_RUNS";
	// jdbcDaoService.deleteAllTables(conn);
	// String sqlChecklength = "Select count(*) from CASIS2_BG_INGEST_RUNS";
	//
	// ResultSet countQuery =
	// conn.createStatement().executeQuery(sqlChecklength);
	// countQuery.next();
	// long length = countQuery.getLong(1);
	//
	// Assert.assertEquals(0, length);
	//
	// }

	@Test
	public void getDocumentLengthTest() throws Exception {
		// prepare data
//		String tableName="CASIS_DOCUMENT";
		
		String sqlCheckLength = "select count(DOCNO) from $tableName";
		String query =sqlCheckLength.replace("$tableName",tableName);
		long lengthTest = 0;
		try (PreparedStatement stmt = conn.prepareStatement(query); ) {
			try (ResultSet rset = stmt.executeQuery();){
			while (rset.next()) {
				lengthTest = rset.getLong(1);
			}
		}
		}
		System.out.println(lengthTest);
		// run function
		long length = jdbcDaoService.getDocumentLength(conn, sqlCheckLength,tableName);

		// check result
		Assert.assertEquals(lengthTest, length);
	}

	@Test
	public void getTotalPageCountTest() throws SQLException {
		int pageCount = jdbcDaoService.getTotalPageCount(conn, 1000,tableName);
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
		String sqlCheckLastRunTable = "select * from CASIS2_BG_INGEST_RUNS2 ";
		try (PreparedStatement ps = conn.prepareStatement(sqlCheckCasisTableIndexTs);
				ResultSet resultSet = ps.executeQuery();) {
			while (resultSet.next()) {
				lastCasisIndexTimestampTest = resultSet.getTimestamp("UPDATE_TIMESTAMP");
			}
		}

		try (PreparedStatement ps = conn.prepareStatement(sqlCheckLastRunTable);
				ResultSet resultSet = ps.executeQuery();) {
			while (resultSet.next()) {
				startTs = resultSet.getTimestamp("START_TS");
				endTs = resultSet.getTimestamp("END_TS");
				lastIngestedRecordTs = resultSet.getTimestamp("LAST_INGESTED_RECORD_TS");
				loadingProcessActive = resultSet.getString("LOADING_PROCESS_ACTIVE");
			}
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
			try (ResultSet rset = stmt.executeQuery();){}
		}

	}

	
	public void queryInsertToRunsTable(Connection conn, String sqlInsert, Timestamp startTs,
			Timestamp lastIngestedRecordTs, String loadingProcessActive) throws SQLException {

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
	
	@Test
	public void queryInsertToRunsTableTest() throws SQLException {
		//prepare the data
		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		
		String sqlInsert = "INSERT INTO CASIS2_BG_INGEST_RUNS2 (START_TS,END_TS,LAST_INGESTED_RECORD_TS,LOADING_PROCESS_ACTIVE) VALUES (?,?,?,?) ";
		String loadingProcessActive = null;
		Timestamp lastIngestedRecordTs = null ;
		Timestamp startTs = null;
		
		String loadingProcessActiveTest="N";
		Timestamp lastIngestedRecordTsTest =new Timestamp(t);
		Timestamp startTsTest=new Timestamp(t);
		 LastHourState lastHourUpdate = new LastHourState();
		 lastHourUpdate.setLastIngestedRecordTs(lastIngestedRecordTsTest);
		//run function
		jdbcDaoService.queryInsertToRunsTable(conn,startTsTest,  lastHourUpdate,loadingProcessActiveTest);
		
		// check results
		String sqlCheckLastRunTable = "select * from CASIS2_BG_INGEST_RUNS2 where START_TS = ?";
		
		try (PreparedStatement ps = conn.prepareStatement(sqlCheckLastRunTable);){
			ps.setTimestamp(1, startTsTest);
			try (ResultSet resultSet = ps.executeQuery();) {
				while (resultSet.next()) {
					startTs = resultSet.getTimestamp("START_TS");
					lastIngestedRecordTs = resultSet.getTimestamp("LAST_INGESTED_RECORD_TS");
					loadingProcessActive = resultSet.getString("LOADING_PROCESS_ACTIVE");
				}
			}	
		}
		
	
		Assert.assertEquals(startTsTest,startTs);
		Assert.assertEquals(lastIngestedRecordTsTest, lastIngestedRecordTs);
		Assert.assertEquals(loadingProcessActiveTest, loadingProcessActive);
		
	
		// delete the database
		String sqlDeleteTestRow = "DELETE CASIS2_BG_INGEST_RUNS2 where START_TS=?";
		try (PreparedStatement ps = conn.prepareStatement(sqlDeleteTestRow);){
			ps.setTimestamp(1, startTs);
			try (ResultSet resultSet = ps.executeQuery();) {
				
			}	
		}
		
	}
	
	
	
	@Test
	public void queryDeleteToCasisDocumentRunsTableTest() throws Exception {
		long length=10;
		
		jdbcDaoService.queryDeleteToCasisDocumentRunsTable(conn);
		String sqlInsertCasisDocumentTable = " select COUNT(*) from CASIS_DOCUMENT_RUNS ";
		try (PreparedStatement stmt = conn.prepareStatement(sqlInsertCasisDocumentTable);
				ResultSet rset = stmt.executeQuery();) {
			while(rset.next()){
				length =rset.getLong(1);
			}
		}
		Assert.assertEquals(0,length);
	}
		
	@Test
	public void checkOralceUpdatesFromLastIngestTest() throws Exception {
		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		Timestamp startTs = new Timestamp(t);
		Timestamp endTs = new Timestamp(t);
		Timestamp lastIngestedRecordTs = null;
		String loadingProcessActive = "Y";

		String sql = "INSERT INTO CASIS2_BG_INGEST_RUNS2 "
				+ "(START_TS, END_TS, LAST_INGESTED_RECORD_TS, LOADING_PROCESS_ACTIVE) VALUES" + "(?,?,?,?)";
		PreparedStatement stmt = conn.prepareStatement(sql);

		String sql1 = "SELECT UPDATE_TIMESTAMP FROM CASIS_DOCUMENT where UPDATE_TIMESTAMP in (select max(UPDATE_TIMESTAMP) from CASIS_DOCUMENT)";
		PreparedStatement stmt1 = conn.prepareStatement(sql1);
		try (ResultSet rset = stmt1.executeQuery();) {
			while (rset.next()) {
				lastIngestedRecordTs = rset.getTimestamp("UPDATE_TIMESTAMP");
			}
		}

		stmt.setTimestamp(1, startTs);
		stmt.setTimestamp(2, endTs);
		stmt.setTimestamp(3, lastIngestedRecordTs);
		stmt.setString(4, loadingProcessActive);

		// get the date from oracle and set it to new database
		try (ResultSet rset = stmt.executeQuery();) {
		}
	}
	
	@Test
	public void queryInsertToCasisDocumentRunsTableTest() throws Exception {
		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		Timestamp startTs = new Timestamp(t);
		Timestamp endTs = new Timestamp(t);
		Timestamp lastIngestedRecordTs = null;
		String loadingProcessActive = "Y";

		String sql = "INSERT INTO CASIS2_BG_INGEST_RUNS2 "
				+ "(START_TS, END_TS, LAST_INGESTED_RECORD_TS, LOADING_PROCESS_ACTIVE) VALUES" + "(?,?,?,?)";
		PreparedStatement stmt = conn.prepareStatement(sql);

		String sql1 = "SELECT UPDATE_TIMESTAMP FROM CASIS_DOCUMENT where UPDATE_TIMESTAMP in (select max(UPDATE_TIMESTAMP) from CASIS_DOCUMENT)";
		PreparedStatement stmt1 = conn.prepareStatement(sql1);
		try (ResultSet rset = stmt1.executeQuery();) {
			while (rset.next()) {
				lastIngestedRecordTs = rset.getTimestamp("UPDATE_TIMESTAMP");
			}
		}

		stmt.setTimestamp(1, startTs);
		stmt.setTimestamp(2, endTs);
		stmt.setTimestamp(3, lastIngestedRecordTs);
		stmt.setString(4, loadingProcessActive);

		try (ResultSet rset = stmt.executeQuery();) {
		}

	}
	
	@After
	public void after() {
		DBManager.closeConn(conn);
		// client.shutdownClient();
	}
}
