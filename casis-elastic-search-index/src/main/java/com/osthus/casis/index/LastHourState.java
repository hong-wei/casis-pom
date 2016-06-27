package com.osthus.casis.index;

import org.json.JSONArray;

public class LastHourState {
	// last hour do not update in oralce , and there are data in Runs_table
	private boolean triggerUpdateIndexFlag;
	private JSONArray casis2bIngestRunsState = new JSONArray();
	private JSONArray lastHourAllTableState = new JSONArray();
	
	
	public boolean isTriggerUpdateIndexFlag() {
		return triggerUpdateIndexFlag;
	}
	public void setTriggerUpdateIndexFlag(boolean triggerUpdateIndexFlag) {
		this.triggerUpdateIndexFlag = triggerUpdateIndexFlag;
	}
	public JSONArray getCasis2bIngestRunsState() {
		return casis2bIngestRunsState;
	}
	public void setCasis2bIngestRunsState(JSONArray casis2bIngestRunsState) {
		this.casis2bIngestRunsState = casis2bIngestRunsState;
	}
	public JSONArray getLastHourAllTableState() {
		return lastHourAllTableState;
	}
	public void setLastHourAllTableState(JSONArray lastHourAllTableState) {
		this.lastHourAllTableState = lastHourAllTableState;
	}


}
