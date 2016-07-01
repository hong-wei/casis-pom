package com.osthus.casis.index;

import java.io.InputStream;
import java.util.Properties;


import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public final class ElasticSearchUtil {

	private static String esserver;

	static{
		Properties props = new Properties();
		InputStream is = ElasticSearchUtil.class.getClassLoader().getResourceAsStream("dbcp.properties");
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
		factory.setHttpClientConfig(
				new HttpClientConfig.Builder(esserver).readTimeout(2000000)
						.multiThreaded(true).build());
		client = factory.getObject();
		return client;
	}
	//TODO 0 refactory --3 I don't know when the client closed : client.shutdownClient();
	public static void close(JestClient client){
		if(client!=null){
			try {
				client.shutdownClient();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
