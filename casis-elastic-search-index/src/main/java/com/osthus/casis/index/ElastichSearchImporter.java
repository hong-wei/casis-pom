package com.osthus.casis.index;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.xml.sax.SAXException;
import de.osthus.ambeth.log.ILogger;
import de.osthus.ambeth.log.LogInstance;

public class ElastichSearchImporter {

	// TODO the difference between the attributes and constructors
	private static Connection conn;
	// private JestClient client;
	private ElasticSearchDao elasticSearchDao = new ElasticSearchDao();
	private JdbcDao jdbcDao = new JdbcDao();
	private JsonUtil jsonUtil = new JsonUtil();
	@LogInstance
	private static ILogger log;

	public ElastichSearchImporter() {
		conn = JdbcUtil.getOraclConnection();
	}

	public void importFromOralce(int pageSize, int pageCount, String casisIndex)
			throws SQLException, FileNotFoundException, JSONException, IOException, TransformerException,
			DocumentException, ParserConfigurationException, SAXException {

		long startTime = System.currentTimeMillis();
		int pageCountRealTime = 0;

		if (pageCount == 0)
			pageCountRealTime = jdbcDao.getTotalPageCount(conn, pageSize);
		else
			pageCountRealTime = pageCount;

		for (int i = 0; i < pageCountRealTime; i++) {
			JSONArray resultSetToJson = jdbcDao.operateByPage(conn, i, pageSize, casisIndex);
			elasticSearchDao.bulkIndex(resultSetToJson, casisIndex);
			System.out.println(
					"This is " + i + " is using " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
		}
		System.out.println("All data is using " + (System.currentTimeMillis() - startTime) / 6000 + " minutes");
	}

	public void importFromOralce(JSONArray resultSetToJson, String casisIndex)
			throws SQLException, FileNotFoundException, JSONException, IOException, TransformerException,
			DocumentException, ParserConfigurationException, SAXException {

		String nos = jsonUtil.getOralceInvalues2(resultSetToJson);
		// TODO max number is 1000
		String sqlDocuments = "select DOCNO,SRC_DB,PART,UPD,DOCUMENT,DATEINSERTED from CASIS_DOCUMENT where DOCNO in("
				+ nos + ")";
		resultSetToJson = jdbcDao.queryToJsonArray(conn, sqlDocuments);

		jdbcDao.addOtherTables(conn, nos, resultSetToJson);

		elasticSearchDao.bulkIndex(resultSetToJson, casisIndex);
	}

	public void updateDataFromOracle() throws Exception {
		Runnable runnable = new Runnable() {
			public void run() {
				
				LastHourState checkLastHourUpdate = jdbcDao.checkLastHourUpdate(conn);

				if (checkLastHourUpdate.isLastHourflag()) {
					try {
						importFromOralce(checkLastHourUpdate.getResultCBIR(), "aa");
						jdbcDao.deleteAllTables(conn);
					} catch (JSONException | SQLException | IOException | TransformerException | DocumentException e) {
						log.error("JSONException | SQLException | IOException | TransformerException | DocumentException", e);
					} catch (Exception e) {
						log.info("Exception", e);
					}
				}
			}
		};
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.SECONDS);
	}

}
