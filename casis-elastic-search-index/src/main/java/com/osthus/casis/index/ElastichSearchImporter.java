package com.osthus.casis.index;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.xml.sax.SAXException;
import de.osthus.ambeth.ioc.annotation.Autowired;
import de.osthus.ambeth.log.ILogger;
import de.osthus.ambeth.log.LogInstance;

public class ElastichSearchImporter {

	@LogInstance
	private static ILogger log;

	@Autowired
	protected JdbcDao jdbcDaoService;

	@Autowired
	protected ElasticSearchDao elasticSearchDaoService;

	@Autowired
	protected JsonUtil jsonUtilService;

	
	private static String esIndexName;

	static {
		Properties props = new Properties();
		InputStream is = ElasticSearchUtil.class.getClassLoader().getResourceAsStream("dbcp.properties");
		try {
			props.load(is);
		} catch (Exception e) {
			log.info("can not find the propertify", e);
		}
		esIndexName = props.getProperty("esindexname");
	}
	
	private int esPageCount = 0;
	private int esPageSize = 1000;

	public void importFromOralce(int pageSize, int pageCount, String casisIndex, String tableName)
			throws SQLException, FileNotFoundException, JSONException, IOException, TransformerException,
			DocumentException, ParserConfigurationException, SAXException {

		Connection conn = DBManager.getConn();
		long startTime = System.currentTimeMillis();
		int pageCountRealTime = 0;

		if (pageCount == 0)
			pageCountRealTime = jdbcDaoService.getTotalPageCount(conn, pageSize, tableName);
		else
			pageCountRealTime = pageCount;
		log.info("total page counts is  " + pageCountRealTime + " pages");
		for (int i = 0; i < pageCountRealTime; i++) {
			JSONArray resultSetToJson = jdbcDaoService.operateByPage(conn, i, pageSize, casisIndex, tableName);
			log.info(casisIndex+" is the index lenght " + resultSetToJson.length());
			elasticSearchDaoService.bulkIndex(resultSetToJson, casisIndex);
			log.info(casisIndex+" is " + i + " is using " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
		}
		log.info("All data is using " + (System.currentTimeMillis() - startTime) / 6000 + " minutes");

		DBManager.closeConn(conn);
	}

	public void importFromOralceUpdateEs(Connection conn, LastHourState lastHourUpdate)
			throws SQLException, FileNotFoundException, JSONException, IOException, TransformerException,
			DocumentException, ParserConfigurationException, SAXException {
		
		Timestamp startTs = getStartTsForPerHourJob();
		
		if (lastHourUpdate.getLastIngestedRecordTs() == null) {
			log.info("initial index data  ---- case1");
			final String tableName = "CASIS_DOCUMENT";
			importFromOralce(esPageSize, esPageCount, esIndexName, tableName);
			String loadingProcessActive = "N";
			lastHourUpdate.setLastIngestedRecordTs(lastHourUpdate.getLastCasisIndexTimestamp());
			jdbcDaoService.queryInsertToRunsTable(conn, startTs, lastHourUpdate, loadingProcessActive);
			jdbcDaoService.queryDeleteToCasisDocumentRunsTable(conn);

		} else if ("N".equals(lastHourUpdate.getLoadingProcessActive())
				&& lastHourUpdate.getLastCasisIndexTimestamp().getTime() >= lastHourUpdate.getEndTs().getTime()) {
			log.info("this hour loading some data, need wait for next hour  ---- case2");
			String loadingProcessActive = "Y";
			jdbcDaoService.queryInsertToRunsTable(conn, startTs, lastHourUpdate, loadingProcessActive);

			System.out.println(lastHourUpdate.getLastCasisIndexTimestamp());
		} else if ("Y".equals(lastHourUpdate.getLoadingProcessActive())
				&& lastHourUpdate.getLastCasisIndexTimestamp().getTime() >= lastHourUpdate.getEndTs().getTime()) {
			log.info("last hour loading some data, this hour still loading data  ---- case3 ");
			String loadingProcessActive = "Y";
			jdbcDaoService.queryInsertToRunsTable(conn, startTs, lastHourUpdate, loadingProcessActive);
			System.out.println(lastHourUpdate.getLastCasisIndexTimestamp());
		} else if ("Y".equals(lastHourUpdate.getLoadingProcessActive())
				&& lastHourUpdate.getLastCasisIndexTimestamp().getTime() < lastHourUpdate.getEndTs().getTime()) {
			log.info(
					"last one hour before finshed loading ,last one hour no loading, start to updata elastic search  ---- case4");
			jdbcDaoService.queryInsertToCasisDocumentRunsTable(conn, lastHourUpdate);

			final String tableName = "CASIS_DOCUMENT_RUNS";
			importFromOralce(esPageSize, esPageCount, esIndexName, tableName);
			String loadingProcessActive = "N";
			lastHourUpdate.setLastIngestedRecordTs(lastHourUpdate.getLastCasisIndexTimestamp());
			jdbcDaoService.queryInsertToRunsTable(conn, startTs, lastHourUpdate, loadingProcessActive);
			jdbcDaoService.queryDeleteToCasisDocumentRunsTable(conn);

		} else {
			log.info("Nothing happen during last two hours  ---- case0");
			String loadingProcessActive = "N";
			jdbcDaoService.queryInsertToRunsTable(conn, startTs, lastHourUpdate, loadingProcessActive);
			System.out.println(lastHourUpdate.getLastCasisIndexTimestamp());
		}
	}

	private Timestamp getStartTsForPerHourJob() {
		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		Timestamp startTs = new Timestamp(t);
		return startTs;
	}
}
