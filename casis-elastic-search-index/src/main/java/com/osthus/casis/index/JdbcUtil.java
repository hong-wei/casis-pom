package com.osthus.casis.index;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

//JDBC Utils : open and close database;
public final class JdbcUtil {
	
	private static String driver;
	private static String url;
	private static String user;
	private static String password;
	
	static{
		Properties props = new Properties();
		InputStream is = JdbcUtil.class.getClassLoader().getResourceAsStream("db.properties");
		try {
			props.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		driver = props.getProperty("driver");
		url = props.getProperty("url");
		user = props.getProperty("user");
		password = props.getProperty("password");
	}
	
	static{
		try {
			Class.forName(driver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getOraclConnection(){
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url,user,password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void close(ResultSet rs){
		if(rs!=null){
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static void close(Statement stmt){
		if(stmt!=null){
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static void close(Connection conn){
		if(conn!=null){
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
