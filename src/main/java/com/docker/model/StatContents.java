package com.docker.model;

import java.util.List;


public class StatContents {
	
	private List<StatContent> lstStCont;
	
	public StatContents(List<StatContent> lst)
	{
		this.lstStCont = lst;
	}

	public List<StatContent> getLstStCont() {
		return lstStCont;
	}

	public void setLstStCont(List<StatContent> lstStCont) {
		this.lstStCont = lstStCont;
	}
	
	

}
