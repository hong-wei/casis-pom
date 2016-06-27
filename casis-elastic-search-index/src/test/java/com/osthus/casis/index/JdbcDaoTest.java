package com.osthus.casis.index;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.osthus.casis.index.ioc.ElasticSearchIocModule;

import de.osthus.ambeth.ioc.annotation.Autowired;
import de.osthus.ambeth.testutil.AbstractIocTest;
import de.osthus.ambeth.testutil.TestModule;
@TestModule(value=ElasticSearchIocModule.class)
public class JdbcDaoTest extends AbstractIocTest {

	// test parameters
	String casisIndex = "casisvm5"; 
	int size = 1000;

	private  Connection conn ;
	
	@Autowired
	protected JdbcDao jdbcDaoService;
	
	@Before
	public void before()   {
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
//		(resultLastHour.length() == 0 && resultCBIR.length() != 0)
		JSONArray casis2bIngestRunsState = checkLastHourUpdate.getCasis2bIngestRunsState();
		JSONArray lastHourAllTableState = checkLastHourUpdate.getLastHourAllTableState();
		boolean triggerUpdateIndexFlag = checkLastHourUpdate.isTriggerUpdateIndexFlag();
		
//		boolean state = resultCBIR.length() 
//		Assert.assertTrue(!triggerUpdateIndexFlag);
		
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
		JSONArray resultSetToJson = jdbcDaoService.queryToJsonArray(conn,sqlDocuments);
		String expectResult = "\"PART\":\"8\",\"UPD\":\"20130928\",\"DOCUMENT\":{\"Document_CASIS-DOCNO\":[\"DGL1483041\"],\"Document_CASIS-UPD_PublicationDate\":[\"28 Sep 2013\"],\"Document_LaunchDetails_CASIS-CO_CASIS-NORMALIZED-CO\":[\"Manufacturer: CHALVER\",\"CHALVER\"],\"Document_LaunchDetails_CASIS-TX_Biotech\":[\"No\"],\"Document_PackInfo_ExcipientInfo_Excipient\":[\"\"],\"Document_LaunchDetails_CASIS-USE_CASIS-ACT_Class_ClassDescription\":[\"Cold Preparations Without Anti-infectives\"],\"Document_LaunchDetails_CASIS-CN_BrandName\":[\"FLUZETRIN F\"],\"Document_PackInfo_DoseFormInfo_DoseForm\":[\"tabs\",\"drops oral\",\"syrup oral\"],\"Document_PackInfo_CASIS-TX_CompositionInfo_Composition\":[\"tabs: cetirizine hydrochloride, 5 mg; paracetamol base, 500 mg; phenylephrine hydrochloride, 10 mg\",\"syrup oral: cetirizine hydrochloride, 2.5 mg/5 ml; paracetamol base, 325 mg/5 ml; phenylephrine hydrochloride, 5 mg/5 ml\",\"drops oral: cetirizine hydrochloride, 1 mg/1 ml; paracetamol base, 100 mg/1 ml; phenylephrine hydrochloride, 10 mg/1 ml\"],\"Document_LaunchDetails_CASIS-USE_CASIS-ACT_Class_ClassCode\":[\"R5A\"],\"Document_LaunchDetails_LaunchDateComment\":[\"\"],\"Document_PackInfo_PriceInfo_Price\":[\"\"],\"Document_LaunchDetails_CASIS-CO_Manufacturer\":[\"CHALVER\"],\"Document_LaunchDetails_CASIS-DSTA_LaunchDate_CCYYMM\":[\"20110201\"],\"Document_LaunchDetails_CASIS-DSTA_LaunchDate\":[\"01 Feb 2011\"],\"Document_LaunchDetails_CASIS-CO_Corporation\":[\"CHALVER\"],\"Document_LaunchDetails_CASIS-RN_CASInfo_CASItem\":[\"\"],\"Document_LaunchDetails_CASIS-USE_CASIS-IND_Indication\":[\"Common cold, influenza.\"],\"Document_LaunchDetails_CASIS-DSTA_Country\":[\"Ecuador\"],\"Document_LaunchDetails_CASIS-DSTA_CASIS-NORMALIZED-DSTA\":[\"EC: Launched 20110201\"],\"Document_PackInfo_NumberOfIngredients\":[\"3\"],\"Document_LaunchDetails_CASIS-TX_Unbranded\":[\"No\"],\"Document_CASIS-UPD_PublicationDate_CCYYMMDD\":[\"20130928\"],\"Document_LaunchDetails_Ingredients_Ingredient\":[\"cetirizine\",\"paracetamol\",\"phenylephrine\"],\"Document_LaunchStatus_RecordStatus\":[\"\"],\"Document_CASIS-MDNUMBER\":[\"MD000001\"],\"DOCUMENT_COMPLETETEXT\":[\"<Document><CASIS-DOCNO>DGL1483041<\\/CASIS-DOCNO><CASIS-UPD><PublicationDate CCYYMMDD=\\\"20130928\\\">28 Sep 2013<\\/PublicationDate><\\/CASIS-UPD><LaunchDetails><LaunchDateComment/><NewChemicalEntity/><Ingredients><Ingredient>cetirizine<\\/Ingredient><Ingredient>paracetamol<\\/Ingredient><Ingredient>phenylephrine<\\/Ingredient><\\/Ingredients><CASIS-USE><CASIS-ACT><Class><ClassCode>R5A<\\/ClassCode><ClassDescription>Cold Preparations Without Anti-infectives<\\/ClassDescription><\\/Class><\\/CASIS-ACT><CASIS-IND><Indication>Common cold, influenza.<\\/Indication><\\/CASIS-IND><\\/CASIS-USE><CASIS-CN><BrandName>FLUZETRIN F<\\/BrandName><\\/CASIS-CN><CASIS-CO><CASIS-NORMALIZED-CO>Manufacturer: CHALVER<\\/CASIS-NORMALIZED-CO><CASIS-NORMALIZED-CO>CHALVER<\\/CASIS-NORMALIZED-CO><Manufacturer>CHALVER<\\/Manufacturer><Corporation>CHALVER<\\/Corporation><\\/CASIS-CO><CASIS-DSTA><CASIS-NORMALIZED-DSTA>EC: Launched 20110201<\\/CASIS-NORMALIZED-DSTA><Country>Ecuador<\\/Country><LaunchDate CCYYMM=\\\"20110201\\\">01 Feb 2011<\\/LaunchDate><\\/CASIS-DSTA><CASIS-TX><Biotech>No<\\/Biotech><Unbranded>No<\\/Unbranded><\\/CASIS-TX><CASIS-RN><CASInfo><CASItem/><\\/CASInfo><\\/CASIS-RN><\\/LaunchDetails><PackInfo><ExcipientInfo><Excipient/><\\/ExcipientInfo><PriceInfo><Price/><\\/PriceInfo><DoseFormInfo><DoseForm>tabs<\\/DoseForm><DoseForm>drops oral<\\/DoseForm><DoseForm>syrup oral<\\/DoseForm><\\/DoseFormInfo><NumberOfIngredients>3<\\/NumberOfIngredients><CASIS-TX><CompositionInfo><Composition>tabs: cetirizine hydrochloride, 5 mg; paracetamol base, 500 mg; phenylephrine hydrochloride, 10 mg<\\/Composition><Composition>syrup oral: cetirizine hydrochloride, 2.5 mg/5 ml; paracetamol base, 325 mg/5 ml; phenylephrine hydrochloride, 5 mg/5 ml<\\/Composition><Composition>drops oral: cetirizine hydrochloride, 1 mg/1 ml; paracetamol base, 100 mg/1 ml; phenylephrine hydrochloride, 10 mg/1 ml<\\/Composition><\\/CompositionInfo><\\/CASIS-TX><\\/PackInfo><LaunchStatus><RecordStatus/><\\/LaunchStatus><CASIS-MDNUMBER>MD000001<\\/CASIS-MDNUMBER><\\/Document>\"],\"Document_LaunchDetails_NewChemicalEntity\":[\"\"]},\"DOCNO\":\"DGL1483041\",\"DATEINSERTED\":\"2016-03-03 09:25:47\",\"SRC_DB\":\"DGL\"";
		String revicerResult = resultSetToJson.toString();
		System.out.println(revicerResult);
		Assert.assertTrue(revicerResult.contains(expectResult));
		
	}
	@Test
	public void deleteAllTablesTest() throws Exception {
		
		String sqlDelete = "truncate table CASIS2_BG_INGEST_RUNS";
		jdbcDaoService.deleteAllTables(conn);
		String sqlChecklength="Select count(*) from CASIS2_BG_INGEST_RUNS";
		
		ResultSet countQuery = conn.createStatement().executeQuery(sqlChecklength);
		countQuery.next();
		long length = countQuery.getLong(1);
		
		Assert.assertEquals(0, length);
		
	}
	
	@Test
	public void deleteAllTables1Test() throws Exception {
		
		String sqlDelete = "truncate table CASIS2_BG_INGEST_RUNS";
	
		jdbcDaoService.deleteAllTables1(conn);
		String sqlChecklength="Select count(*) from CASIS2_BG_INGEST_RUNS";
		
		ResultSet countQuery = conn.createStatement().executeQuery(sqlChecklength);
		countQuery.next();
		long length = countQuery.getLong(1);
		
		Assert.assertEquals(0, length);
		
	}
	
	@Test
	public void  getDocumentLengthTest() throws Exception {
		String sqlCheckLength= "select count(DOCNO) from CASIS_DOCUMENT";
		long length= jdbcDaoService.getDocumentLength(conn, sqlCheckLength);
		Assert.assertEquals(1602141, length);
	}
	
	@Test
	public void getTotalPageCountTest() throws SQLException{
		int pageCount = jdbcDaoService.getTotalPageCount(conn,1000);
		Assert.assertEquals(1603, pageCount);
	}

	@After
	public void after() {
		DBManager.closeConn(conn);
//	    client.shutdownClient();
	}
}
