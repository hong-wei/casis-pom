package com.osthus.casis.index;

import org.junit.Assert;
import org.junit.Test;

import com.osthus.casis.index.ioc.ElasticSearchIocModule;

import de.osthus.ambeth.log.ILogger;
import de.osthus.ambeth.log.LogInstance;
import de.osthus.ambeth.testutil.AbstractIocTest;
import de.osthus.ambeth.testutil.TestFrameworkModule;
import io.searchbox.client.JestClient;

@TestFrameworkModule(value = ElasticSearchIocModule.class)
public class ElasticSearchUtilTest extends AbstractIocTest {

	@LogInstance
	private static ILogger log;

	@Test
	public void getEsClientTest() {
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			JestClient esClient = ElasticSearchUtil.getEsClient();
			log.info(i + "   ");
			ElasticSearchUtil.close(esClient);
		}
		long end = System.currentTimeMillis();

		Assert.assertTrue((end - begin) > 0);
	}
}
