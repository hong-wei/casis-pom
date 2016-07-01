package com.osthus.casis.index;

import org.json.JSONArray;
import java.io.IOException;
import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import org.json.JSONObject;

import de.osthus.ambeth.ioc.annotation.Autowired;

public class ElasticSearchDao {

	//TODO 0 refactory --3 I don't know when the client closed : client.shutdownClient();
	private JestClient client;
	
	public ElasticSearchDao() {
		client = ElasticSearchUtil.getEsClinet();
	}

	public void bulkIndex(JSONArray resultSetToJson, String esIndexName) throws IOException {
		Bulk.Builder bulkBuilder = new Bulk.Builder();
		for (Object obj : resultSetToJson) {
			JSONObject docJson = (JSONObject) obj;
			Index index = new Index.Builder(docJson.toString()).index(esIndexName).type("documents").id(docJson.get("DOCNO").toString()).build();
			bulkBuilder.addAction(index);
			
		}
		client.execute(bulkBuilder.build());
		
	}

}
