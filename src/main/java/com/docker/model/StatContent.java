package com.docker.model;

public class StatContent {
	private static final long serialVersionUID = 6179861720292153126L; 
	private String statKey;
	private long[] timestamps;
	private Double[] data;
	private String[] values;
	
	public String getStatKey() {
		return statKey;
	}
	public void setStatKey(String statKey) {
		this.statKey = statKey;
	}
	public long[] getTimestamps() {
		return timestamps;
	}
	public void setTimestamps(long[] timestamps) {
		this.timestamps = timestamps;
	}
	public Double[] getData() {
		return data;
	}
	public void setData(Double[] data) {
		this.data = data;
	}
	public String[] getValues() {
		return values;
	}
	public void setValues(String[] values) {
		this.values = values;
	}
	
	

}
