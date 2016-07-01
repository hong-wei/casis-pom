package com.osthus.casis.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.dom4j.DocumentException;
import org.json.JSONException;
import org.xml.sax.SAXException;

import de.osthus.ambeth.ioc.annotation.Autowired;
import de.osthus.ambeth.job.IJob;
import de.osthus.ambeth.job.IJobContext;
import de.osthus.ambeth.log.ILogger;
import de.osthus.ambeth.log.LogInstance;

public class MyJobUpdateEachHour implements IJob {

	// TODO add more tags
	@LogInstance
	private static ILogger log;

	@Autowired
	protected JdbcDao jdbcDaoService;

	@Autowired
	protected ElastichSearchImporter elastichSearchImporter;

	@Autowired
	protected ElasticSearchDao elasticSearchDaoService;

	@Override
	public boolean canBePaused() {
		return false;
	}

	@Override
	public boolean canBeStopped() {
		return false;
	}

	@Override
	public boolean supportsStatusTracking() {
		return false;
	}

	@Override
	public boolean supportsCompletenessTracking() {
		return false;
	}

	@Override
	public void execute(IJobContext context) throws SQLException {

		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		Timestamp startTs = new Timestamp(t);

		Connection conn = DBManager.getConn();
		LastHourState lastHourUpdate = jdbcDaoService.queryLastTsFromCasisTableAndPreviousRunTable(conn);
		try {
			elastichSearchImporter.importFromOralceUpdateEs(conn, lastHourUpdate, startTs);
			log.info("update the date to ES");
		} catch (JSONException | SQLException | IOException | TransformerException | DocumentException
				| ParserConfigurationException | SAXException e) {
			log.info("Update exception", e);
		}

		DBManager.closeConn(conn);
	}
}