package com.osthus.casis.index;

import org.json.JSONArray;

public class LastHourState {
	private boolean lastHourflag;
	public boolean isLastHourflag() {
		return lastHourflag;
	}
	public void setLastHourflag(boolean lastHourflag) {
		this.lastHourflag = lastHourflag;
	}
	public JSONArray getResultCBIR() {
		return resultCBIR;
	}
	public void setResultCBIR(JSONArray resultCBIR) {
		this.resultCBIR = resultCBIR;
	}
	public JSONArray getResultLastHour() {
		return resultLastHour;
	}
	public void setResultLastHour(JSONArray resultLastHour) {
		this.resultLastHour = resultLastHour;
	}
	private JSONArray resultCBIR = new JSONArray();
	private JSONArray resultLastHour = new JSONArray();

}
