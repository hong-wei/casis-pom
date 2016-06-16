package com.osthus.casis.index;

import java.io.InputStream;
import java.util.Properties;


import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

//JDBC Utils : open and close database;
public final class ElasticSearchUtil {

	private static String esserver;

	static{
		Properties props = new Properties();
		InputStream is = ElasticSearchUtil.class.getClassLoader().getResourceAsStream("db.properties");
		try {
			props.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		esserver = props.getProperty("esserver");
	}
	
	public static JestClient getEsClinet(){
		JestClient client = null;
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig( // http://localhost:9200/
				//TODO not sure what means the readTimeout
				new HttpClientConfig.Builder(esserver).readTimeout(2000000)
						.multiThreaded(true).build());
		client = factory.getObject();
		return client;
	}

	//TODO need close or not ??
//	public static void close(Connection conn){
//		if(conn!=null){
//			try {
//				conn.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
