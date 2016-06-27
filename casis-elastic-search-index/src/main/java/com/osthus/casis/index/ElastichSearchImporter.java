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
	
	public void importFromOralce(int pageSize, int pageCount, String casisIndex)
			throws SQLException, FileNotFoundException, JSONException, IOException, TransformerException,
			DocumentException, ParserConfigurationException, SAXException {
		//TODO 0 refactory --6 where to start the ambeth??
		Connection conn=DBManager.getConn();  
		long startTime = System.currentTimeMillis();
		int pageCountRealTime = 0;

		if (pageCount == 0)
			pageCountRealTime = jdbcDaoService.getTotalPageCount(conn, pageSize);
		else
			pageCountRealTime = pageCount;

		for (int i = 0; i < pageCountRealTime; i++) {
			JSONArray resultSetToJson = jdbcDaoService.operateByPage(conn, i, pageSize, casisIndex);
			elasticSearchDaoService.bulkIndex(resultSetToJson, casisIndex);
			System.out.println(
					"This is " + i + " is using " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
		}
		System.out.println("All data is using " + (System.currentTimeMillis() - startTime) / 6000 + " minutes");
		
		DBManager.closeConn(conn); 
	}

	public void importFromOralce(Connection conn,JSONArray resultSetToJson, String casisIndex)
			throws SQLException, FileNotFoundException, JSONException, IOException, TransformerException,
			DocumentException, ParserConfigurationException, SAXException {
		
		String nos = jsonUtilService.getOralceInvalues2(resultSetToJson);
		// TODO max number is 1000
		String sqlDocuments = "select DOCNO,SRC_DB,PART,UPD,DOCUMENT,DATEINSERTED from CASIS_DOCUMENT where DOCNO in("
				+ nos + ")";
		System.out.println(sqlDocuments);
		
		resultSetToJson = jdbcDaoService.queryToJsonArray(conn, sqlDocuments);

		jdbcDaoService.addOtherTables(conn, nos, resultSetToJson);
		System.out.println(resultSetToJson);
		
		elasticSearchDaoService.bulkIndex(resultSetToJson, casisIndex);
	}


}
