package com.osthus.casis.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.transform.TransformerException;

import org.dom4j.DocumentException;
import org.json.JSONException;

import de.osthus.ambeth.ioc.annotation.Autowired;
import de.osthus.ambeth.job.IJob;
import de.osthus.ambeth.job.IJobContext;
import de.osthus.ambeth.log.ILogger;
import de.osthus.ambeth.log.LogInstance;

public class MyJobUpdateEachHour implements IJob
{
	
	//TODO add more tags
	@LogInstance
	private static ILogger log;
	
	@Autowired
	protected JdbcDao jdbcDaoService;
	
	@Autowired
	protected ElastichSearchImporter elastichSearchImporter;
	
	@Autowired
	protected ElasticSearchDao elasticSearchDaoService;
	
	@Override
	public boolean canBePaused()
	{
		return false;
	}

	@Override
	public boolean canBeStopped()
	{
		return false;
	}

	@Override
	public boolean supportsStatusTracking()
	{
		return false;
	}

	@Override
	public boolean supportsCompletenessTracking()
	{
		return false;
	}

	@Override
	public void execute(IJobContext context) throws Throwable
	{
		System.out.println(Thread.currentThread().getId() + " current time:" + new Date());
		
		Connection conn=DBManager.getConn();  
		LastHourState checkLastHourUpdate = jdbcDaoService.checkLastHourUpdate(conn);
		//
//		if (checkLastHourUpdate.isTriggerUpdateIndexFlag()) {
		if (true){ // test
			try {
				elastichSearchImporter.importFromOralce(conn,checkLastHourUpdate.getCasis2bIngestRunsState(), "aa");
//				jdbcDaoService.deleteAllTables(conn);
				log.info("update the date to ES"); 
				System.out.println("update the date to ES");
			} catch (JSONException | SQLException | IOException | TransformerException | DocumentException e) {
				log.error("JSONException | SQLException | IOException | TransformerException | DocumentException", e);
			} catch (Exception e) {
				log.info("Exception", e);
			}
		}
		DBManager.closeConn(conn); 
	}

}