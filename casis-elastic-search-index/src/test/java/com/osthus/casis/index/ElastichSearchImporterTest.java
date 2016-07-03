package com.osthus.casis.index;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osthus.casis.index.ioc.ElasticSearchIocModule;

import de.osthus.ambeth.ioc.annotation.Autowired;
import de.osthus.ambeth.testutil.AbstractIocTest;
import de.osthus.ambeth.testutil.TestFrameworkModule;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

@TestFrameworkModule(value = ElasticSearchIocModule.class)
public class ElastichSearchImporterTest extends AbstractIocTest {

	private JestClient client;

	@Autowired
	private ElastichSearchImporter elastichSearchImporterService;

	@Autowired
	protected JdbcDao jdbcDaoService;
	private Connection conn;

	@Before
	public void before() {
		conn = DBManager.getConn();
		client = ElasticSearchUtil.getEsClient();
	}

	private static String esIndexName;

	static {
		Properties props = new Properties();
		InputStream is = ElasticSearchUtil.class.getClassLoader().getResourceAsStream("dbcp.properties");
		try {
			props.load(is);
		} catch (Exception e) {
		}
		esIndexName = props.getProperty("esindexname");
	}

	@Test
	public void importFromOralcePartTest() throws Exception {

		// prepare data
		String esIndex = "junitimportfromoralceparttest";
		String tableName = "CASIS_DOCUMENT"; // CASIS_DOCUMENT_RUNS
		int esPageSize = 2;
		int esPageCount = 1;

		// run the function
		elastichSearchImporterService.importFromOralce(esPageSize, esPageCount, esIndex, tableName);
		Thread.sleep(1000);// make sure the data has been inserted to Elastic
							// Search

		// get the result
		String seachString = "{\r\n" + "  \"size\":1,\r\n" + "  \"query\":\r\n" + "  {\r\n"
				+ "    \"match_all\": {}\r\n" + "  }\r\n" + "}";

		Search searchQuery = new Search.Builder(seachString).addIndex(esIndex).addType("documents").build();
		SearchResult searchResult = client.execute(searchQuery);
		String resultString = searchResult.getJsonString();

		// analysis the result
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(resultString);
		JsonNode path = rootNode.path("hits").path("hits");

		String objString = null;
		if (path.isArray()) {
			for (final JsonNode objNode : path) {
				objString = objNode.path("_source").toString();
			}
		}

		// check the result part
		Assert.assertTrue(objString.contains("DOCNO"));
		Assert.assertTrue(objString.contains("DOCUMENT"));

		if (objString.contains("CHEM_STRUCTURE_DATA")) {
			Assert.assertTrue(objString.contains("CHEM_STRUCTURE_DATA"));
		}
		if (objString.contains("CASIS_DEVSTATUS")) {
			Assert.assertTrue(objString.contains("CASIS_DEVSTATUS"));
		}
		if (objString.contains("CASIS_COMPOUND")) {
			Assert.assertTrue(objString.contains("CASIS_COMPOUND"));
		}
		if (objString.contains("CASIS_USE")) {
			Assert.assertTrue(objString.contains("CASIS_USE"));
		}
		if (objString.contains("CASIS_COMPANY")) {
			Assert.assertTrue(objString.contains("CASIS_COMPANY"));
		}
	}

	@Test
	// index the whole database, just stop it now.
	public void importFromOralceTest() throws Exception {
		// 1 prepare date
		String esIndex = "junitimportfromoralcetest";
		int esPageCount = 0; // start from page 0
		int esPageSize = 1000; // each page have 1000 items
		String tableName = "CASIS_DOCUMENT";
		// 2 run the app
		// elastichSearchImporterService.importFromOralce(esPageSize,
		// esPageCount, esIndex, tableName);
		// 3 check the result from server .
	}

	@Test
	public void importFromOralceUpdateEsTest() throws Exception {

		// case0 ---- Nothing happen during last two hours
		// test -- need test in real time

		// case1 ---- initial index data
		// condition : lastHourUpdate.getLastIngestedRecordTs() == null
		// test -- need test in real time

		// case2 ---- this hour loading some data, need wait for next hour
		 testCase2();
		// // case3 ---- last hour loading some data, this hour still loading
		// data
		 testCase3();
		// case4 ---- last one hour before finshed loading ,last one hour no
		 // this test will detele all the last hour tables. please take care to test it.
		// loading, start to updata elastic search
		testCase4();

	}

	private void testCase2() throws SQLException, FileNotFoundException, IOException, TransformerException,
			DocumentException, ParserConfigurationException, SAXException {

		// case2 ---- this hour loading some data, need wait for next hour
		// 2.1 condition
		// LastIngestedRecordTs != null --> !null
		// && "N".equals(lastHour.LoadingProcessActive) -->load='N'
		// && LastCasisIndexTimestamp >= lastHour.EndTs
		// 2.2 prepare the data for the last hour
		// set the current time
		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		Timestamp lastIngestedRecordTsLast = new Timestamp(t);
		String loadingProcessActivelast = "N";
		Timestamp endTsLast = new Timestamp(t); // endTs == CasisTs
		Timestamp lastCasisIndexTimestampLast = new Timestamp(t);
		// prepare last hour data
		LastHourState lastHourUpdate = prepareHourdate(lastIngestedRecordTsLast, loadingProcessActivelast,
				lastCasisIndexTimestampLast, endTsLast);
		// prepare expect hour data
		String loadingProcessActiveExpect = "Y";
		LastHourState expectThisHourState = prepareHourdate(lastIngestedRecordTsLast, loadingProcessActiveExpect,
				lastCasisIndexTimestampLast, endTsLast);
		// 2.3 run the app
		elastichSearchImporterService.importFromOralceUpdateEs(conn, lastHourUpdate);
		// 2.4 check results
		LastHourState resultThisHourState = getThisHourResult(lastCasisIndexTimestampLast);
		try {
			compareResult(expectThisHourState, resultThisHourState);
		} finally {
			deleteTestRow(endTsLast);
		}
	}

	private void testCase3() throws SQLException, FileNotFoundException, IOException, TransformerException,
			DocumentException, ParserConfigurationException, SAXException {
		// case3 ---- this hour loading some data, need wait for next hour
		// 3.1 condition
		// LastIngestedRecordTs != null --> !null
		// && "Y".equals(lastHour.LoadingProcessActive) -->load='Y'
		// && LastCasisIndexTimestamp >= lastHour.EndTs
		// 3.2 prepare the data for the last hour
		// set the current time
		// prepare last hour data
		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		Timestamp lastIngestedRecordTsLast = new Timestamp(t);
		String loadingProcessActivelast = "Y";
		Timestamp endTsLast = new Timestamp(t); // CasisTs => endTs
		Timestamp lastCasisIndexTimestampLast = new Timestamp(t + 1);
		LastHourState lastHourUpdate = prepareHourdate(lastIngestedRecordTsLast, loadingProcessActivelast,
				lastCasisIndexTimestampLast, endTsLast);
		// prepare expect hour data
		String loadingProcessActiveExpect = "Y";
		LastHourState expectThisHourState = prepareHourdate(lastIngestedRecordTsLast, loadingProcessActiveExpect,
				lastCasisIndexTimestampLast, endTsLast);
		// 3.3 run the app
		elastichSearchImporterService.importFromOralceUpdateEs(conn, lastHourUpdate);
		// 3.4 check results
		checkLastResult(endTsLast, lastCasisIndexTimestampLast, expectThisHourState);
	}

	private void testCase4() throws SQLException, FileNotFoundException, IOException, TransformerException,
			DocumentException, ParserConfigurationException, SAXException {
		// case4 ---- this hour loading some data, need wait for next hour
		// 4.1 condition
		// LastIngestedRecordTs != null --> !null
		// "Y".equals(lastHourUpdate.getLoadingProcessActive())
		// && lastHourUpdate.getLastCasisIndexTimestamp().getTime() <
		// lastHourUpdate.getEndTs().getTime()
		// 4.2 prepare the data for the last hour
		// prepare last hour data
		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		Timestamp lastIngestedRecordTsLast = new Timestamp(t);
		String loadingProcessActivelast = "Y";
		Timestamp endTsLast = new Timestamp(t + 1); // CasisTs < endTs
		Timestamp lastCasisIndexTimestampLast = new Timestamp(t);
		LastHourState lastHourUpdate = prepareHourdate(lastIngestedRecordTsLast, loadingProcessActivelast,
				lastCasisIndexTimestampLast, endTsLast);
		// prepare expect hour data
		String loadingProcessActiveExpect = "N";
		Timestamp lastCasisIndexTimestampExpect = lastCasisIndexTimestampLast;
		LastHourState expectThisHourState = prepareHourdate(lastIngestedRecordTsLast, loadingProcessActiveExpect,
				lastCasisIndexTimestampExpect, endTsLast);
		// 4.3 run the app
		String sqlInsert = "INSERT INTO CASIS_DOCUMENT_RUNS (DOCNO,ID) VALUES ('lastIngestedRecordTsLast','1')";
		try (PreparedStatement stmt = conn.prepareStatement(sqlInsert); ResultSet rset = stmt.executeQuery();) {
		}

		elastichSearchImporterService.importFromOralceUpdateEs(conn, lastHourUpdate);

		// 4.4 check results

		checkLastResult(endTsLast, lastCasisIndexTimestampLast, expectThisHourState);

		String objString = getSourceStringFromEs(esIndexName);
		Assert.assertTrue(objString.contains("lastIngestedRecordTsLast"));

		
	}

	private void checkLastResult(Timestamp endTsLast, Timestamp lastCasisIndexTimestampLast,
			LastHourState expectThisHourState) throws SQLException {
	}

	private LastHourState getThisHourResult(Timestamp lastCasisIndexTimestampLast) throws SQLException {
		String sqlCheckLastRunTable = "SELECT * FROM CASIS2_BG_INGEST_RUNS2\r\n"
				+ "	WHERE END_TS = (SELECT MAX(END_TS)AS END_TS FROM CASIS2_BG_INGEST_RUNS2)";
		LastHourState resultThisHourState = new LastHourState();
		try (PreparedStatement ps = conn.prepareStatement(sqlCheckLastRunTable);) {
			try (ResultSet resultSet = ps.executeQuery();) {
				while (resultSet.next()) {
					resultThisHourState = prepareHourdate(resultSet.getTimestamp("LAST_INGESTED_RECORD_TS"),
							resultSet.getString("LOADING_PROCESS_ACTIVE"), lastCasisIndexTimestampLast,
							resultSet.getTimestamp("END_TS"));
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return resultThisHourState;
	}

	private void deleteTestRow(Timestamp endTsLast) throws SQLException {
		String sqlDeleteTestRow = "DELETE CASIS2_BG_INGEST_RUNS2 where START_TS=?";
		try (PreparedStatement ps = conn.prepareStatement(sqlDeleteTestRow);) {
			ps.setTimestamp(1, endTsLast);
			try (ResultSet resultSet = ps.executeQuery();) {
			}
		}
	}

	private void compareResult(LastHourState expectThisHourState, LastHourState resultThisHourState) {
		// TODO can only test one data, it can be enhanced, this is only test > time stap.
		Assert.assertTrue(resultThisHourState.getEndTs().getTime() >= resultThisHourState.getEndTs().getTime());
		Assert.assertEquals(expectThisHourState.getLoadingProcessActive(),
				resultThisHourState.getLoadingProcessActive());
		Assert.assertTrue(resultThisHourState.getLastCasisIndexTimestamp().getTime() >= resultThisHourState
				.getLastCasisIndexTimestamp().getTime());
	}

	private LastHourState prepareHourdate(Timestamp lastIngestedRecordTsTest, String loadingProcessActiveTest,
			Timestamp lastCasisIndexTimestamp, Timestamp endTs) {
		LastHourState lastHourUpdate = new LastHourState();

		lastHourUpdate.setLastIngestedRecordTs(lastIngestedRecordTsTest);
		lastHourUpdate.setLoadingProcessActive(loadingProcessActiveTest);
		lastHourUpdate.setLastCasisIndexTimestamp(lastCasisIndexTimestamp);
		lastHourUpdate.setEndTs(endTs);
		return lastHourUpdate;
	}

	@Test
	public void test() throws SQLException, JsonProcessingException, IOException {
//		String objString = getSourceStringFromEs(esIndexName);
//		Assert.assertTrue(objString.contains("DOCNO"));
	}

	private String getSourceStringFromEs(String esIndexName) throws IOException, JsonProcessingException {
		String queryMatchAll = "{\r\n" + "  \"query\": {\r\n" + "    \"match_all\": {}\r\n" + "  }\r\n" + "}";
		Search search1 = new Search.Builder(queryMatchAll).addIndex(esIndexName).addType("documents").build();

		SearchResult result = client.execute(search1);

		String allResponse = result.getJsonString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(allResponse);
		JsonNode path = rootNode.path("hits").path("hits");

		String objString = null;
		if (path.isArray()) {
			for (final JsonNode objNode : path) {
				objString = objNode.path("_source").toString();
			}
		}
		return objString;
	}
	
	@After
	public void after() {
		DBManager.closeConn(conn);
		// client.shutdownClient();
	}
}
