package com.osthus.casis.index;

import org.json.JSONArray;
import java.io.IOException;
import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import org.json.JSONObject;

public class ElasticSearchDao {

	private JestClient client;

	public ElasticSearchDao() {
		client = ElasticSearchUtil.getEsClinet();
	}

	public void bulkIndex(JSONArray resultSetToJson, String esIndexName) throws IOException {
		Bulk.Builder bulkBuilder = new Bulk.Builder();
		// System.out.println("length is " + resultSetToJson.length());
		for (Object obj : resultSetToJson) {
			JSONObject docJson = (JSONObject) obj;
			// System.out.println(docJson);
			Index index = new Index.Builder(docJson.toString()).index(esIndexName).type("documents").build();
			bulkBuilder.addAction(index);
		}
		client.execute(bulkBuilder.build());
	}

}
