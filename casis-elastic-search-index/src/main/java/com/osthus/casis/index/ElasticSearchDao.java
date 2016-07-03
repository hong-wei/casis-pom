package com.osthus.casis.index;

import org.json.JSONArray;
import java.io.IOException;
import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import org.json.JSONObject;

import de.osthus.ambeth.log.ILogger;
import de.osthus.ambeth.log.LogInstance;

public class ElasticSearchDao {

	@LogInstance
	private static ILogger log;

	private JestClient client;
	
	public ElasticSearchDao() {
		client = ElasticSearchUtil.getEsClient();
	}

	public void bulkIndex(JSONArray resultSetToJson, String esIndexName)   {
		Bulk.Builder bulkBuilder = new Bulk.Builder();
		for (Object obj : resultSetToJson) {
			JSONObject docJson = (JSONObject) obj;
			Index index = new Index.Builder(docJson.toString()).index(esIndexName).type("documents").id(docJson.get("DOCNO").toString()).build();
			bulkBuilder.addAction(index);
		}
		try {
			client.execute(bulkBuilder.build());
		} catch (IOException e) {
			log.info("After backend processing ,Elastic Seach index exception", e);
		}
	}

}
