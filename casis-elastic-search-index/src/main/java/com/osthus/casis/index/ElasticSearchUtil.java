package com.osthus.casis.index;

import java.io.InputStream;
import java.util.Properties;

import de.osthus.ambeth.log.ILogger;
import de.osthus.ambeth.log.LogInstance;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public final class ElasticSearchUtil {

	@LogInstance
	private static ILogger log;

	private static String esserver;

	static {
		Properties props = new Properties();
		InputStream is = ElasticSearchUtil.class.getClassLoader().getResourceAsStream("dbcp.properties");
		try {
			props.load(is);
		} catch (Exception e) {
			log.info("can not find the propertify", e);
		}
		esserver = props.getProperty("esserver");
	}
	private ElasticSearchUtil() {
	}

	public static JestClient getEsClient() {
		JestClient client = null;
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(
				new HttpClientConfig.Builder(esserver).readTimeout(2000000).multiThreaded(true).build());
		client = factory.getObject();
		return client;
	}

	public static void close(JestClient client) {
		if (client != null) {
			try {
				client.shutdownClient();
			} catch (Exception e) {
				log.info("The JestClinet is not closed properly", e);
			}
		}
	}
}
