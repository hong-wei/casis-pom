package com.osthus.casis.index;

import java.sql.Timestamp;

import org.json.JSONArray;

public class LastHourState {
	private Timestamp lastCasisIndexTimestamp = null;
	private Timestamp startTs = null;
	private Timestamp endTs = null;
	private Timestamp lastIngestedRecordTs = null;
	private String loadingProcessActive = null;
	
	private JSONArray allRowsFromLastESIngest = new JSONArray();

	public Timestamp getLastCasisIndexTimestamp() {
		return lastCasisIndexTimestamp;
	}

	public void setLastCasisIndexTimestamp(Timestamp lastCasisIndexTimestamp) {
		this.lastCasisIndexTimestamp = lastCasisIndexTimestamp;
	}

	public Timestamp getStartTs() {
		return startTs;
	}

	public void setStartTs(Timestamp startTs) {
		this.startTs = startTs;
	}

	public Timestamp getEndTs() {
		return endTs;
	}

	public void setEndTs(Timestamp endTs) {
		this.endTs = endTs;
	}

	public Timestamp getLastIngestedRecordTs() {
		return lastIngestedRecordTs;
	}

	public void setLastIngestedRecordTs(Timestamp lastIngestedRecordTs) {
		this.lastIngestedRecordTs = lastIngestedRecordTs;
	}

	public String getLoadingProcessActive() {
		return loadingProcessActive;
	}

	public void setLoadingProcessActive(String loadingProcessActive) {
		this.loadingProcessActive = loadingProcessActive;
	}

	public JSONArray getAllRowsFromLastESIngest() {
		return allRowsFromLastESIngest;
	}

	public void setAllRowsFromLastESIngest(JSONArray allRowsFromLastESIngest) {
		this.allRowsFromLastESIngest = allRowsFromLastESIngest;
	}
	
	

}
