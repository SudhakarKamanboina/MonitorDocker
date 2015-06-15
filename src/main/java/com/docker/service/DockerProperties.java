package com.docker.service;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vmware.ops.api.model.stat.StatContent;
import com.vmware.ops.api.model.stat.StatContents;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

public class DockerProperties {
	
	private String url;
	
	DockerProperties(String strUrl)
	{
		this.url = strUrl;
	}
	
	void collectProperties()
	{
		String newUrl = url.substring(0, url.lastIndexOf("8")+1);
		newUrl = newUrl+"/images/json";
		
		HttpRequest httpRequest = HttpRequest.get(newUrl);
		httpRequest.header("X-vRealizeOps-API-use-unsupported", "true");
		httpRequest.basicAuthentication("admin", "Login@123");
		HttpResponse response = httpRequest.send();
		JSONArray prop = new JSONArray(response.body());
		constructRequestBody(prop);
	}
	
	void constructRequestBody(JSONArray prop)
	{
		Date dt;
		StatContents sts = new StatContents();
		for(int i=0; i<prop.length(); i++)
		{
			
			JSONObject obj = prop.getJSONObject(i);
			String id = obj.getString("Id");
			JSONArray repoTag = (JSONArray) obj.get("RepoTags");
			dt = new Date();
			
			
			StatContent st = new StatContent();
			st.setStatKey("config|imageId");
			st.setTimestamps(new long[]{dt.getTime()});
			st.setValues(new String[]{id});
			
			StatContent st1 = new StatContent();
			st1.setStatKey("config|repoTag");
			st1.setTimestamps(new long[]{dt.getTime()});
			String val[] = new String[repoTag.length()];
			for(int j=0; j<repoTag.length(); j++)
				val[j] = repoTag.getString(j);
			st1.setValues(val);
			
			sts.addStatContent(st);
			sts.addStatContent(st1);
			
		}
		
		pushProperties(new JSONObject(sts).toString().replaceFirst("statContents", "property-content"));
	}
	
	void pushProperties(String requestBody)
	{
		HttpRequest httpRequest = HttpRequest.post("https://10.20.133.250/suite-api//api/resources/d0d969ca-c8cf-4195-a1a0-80a300efa4c4/properties");
		httpRequest.body(requestBody);
		httpRequest.contentType("application/json");
		httpRequest.header("X-vRealizeOps-API-use-unsupported", "true");
		httpRequest.basicAuthentication("admin", "Login@123");
		HttpResponse response = httpRequest.send();
		System.out.println("this is for properties: "+response.statusCode()+" : "+response.statusPhrase());
	}
	

}
