package com.osthus.casis.index;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DBManager {
	private static final Log log = LogFactory.getLog(DBManager.class);
	private static final String configFile = "dbcp.properties";
	private static DataSource dataSource;
	static {
		Properties dbProperties = new Properties();
		try {
			dbProperties.load(DBManager.class.getClassLoader().getResourceAsStream(configFile));
			dataSource = BasicDataSourceFactory.createDataSource(dbProperties);
			Connection conn = getConn();
			DatabaseMetaData mdm = conn.getMetaData();
			log.info("Connected to " + mdm.getDatabaseProductName() + " " + mdm.getDatabaseProductVersion());
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			log.error("initilasition failed" + e);
		}
	}

	private DBManager() {
	}

	
	public static final Connection getConn() {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			log.error("get connect filed" + e);
		}
		return conn;
	}


	public static void closeConn(Connection conn) {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.setAutoCommit(true);
				conn.close();
			}
		} catch (SQLException e) {
			log.error("colse connction failed" + e);
		}
	}
}