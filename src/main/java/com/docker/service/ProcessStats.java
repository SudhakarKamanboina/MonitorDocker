package com.docker.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.vmware.ops.api.model.stat.StatContent;
import com.vmware.ops.api.model.stat.StatContents;

public class ProcessStats implements Callable<Boolean> {

	List<String> lstDockerStat = null;

	public ProcessStats(List<String> strDockerStat) {
		this.lstDockerStat = strDockerStat;
	}

	@Override
	public Boolean call() {
		String jsonString = ConstructJson();
		pushStats(jsonString);
		return true;
	}

	private void pushStats(String jsonString) {
		try
		{
			System.out.println(jsonString);
			HttpRequest httpRequest = HttpRequest.post("https://10.20.133.250/suite-api//api/resources/d0d969ca-c8cf-4195-a1a0-80a300efa4c4/stats");
			httpRequest.body(jsonString);
			httpRequest.contentType("application/json");
			httpRequest.header("X-vRealizeOps-API-use-unsupported", "true");
			httpRequest.basicAuthentication("admin", "Login@123");
			HttpResponse response = httpRequest.send();
			System.out.println("this is for container: "+response.statusCode()+" : "+response.statusPhrase());
			
			StatContents sts = null;
			List<Long> timestamps = getTimestamps();
			StatContent st = new StatContent();
			st.setStatKey("cpu");
			st.setData(new double[] { 5, 5, 5 });
			st.setTimestamps(new long[] { timestamps.get(0), timestamps.get(1), timestamps.get(2) });
			sts = new StatContents(Arrays.asList(st));
			String str = new JSONObject(sts).toString().replaceFirst("statContents", "stat-content");
			System.out.println("HOST "+str);
			
			HttpRequest httpRequest1 = HttpRequest.post("https://10.20.133.250/suite-api//api/resources/16b3ecbe-b36c-4053-84ae-1122949fea60/stats");
			httpRequest1.body(str);
			httpRequest1.contentType("application/json");
			httpRequest1.header("X-vRealizeOps-API-use-unsupported", "true");
			httpRequest1.basicAuthentication("admin", "Login@123");
			HttpResponse response1 = httpRequest1.send();
			System.out.println("this is for HOST: "+response1.statusCode()+" : "+response1.statusPhrase());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	double RoundTo2Decimals(double val) {
		//TODO: require better approach for decimal round up
		String strVal = String.valueOf(val).substring(0, 4);
		return new Double(strVal);
	}

	private String ConstructJson() {
		StatContents sts = null;
		try {

			List<Long> timestamps = getTimestamps();
			List<Double> cpuStats = getCpuStat();
			List<Double> netStats = getNetStat();
			List<Double> memStats = getMemStat();
			
			System.out.println(timestamps.size());
			System.out.println(cpuStats.size());

			StatContent st = new StatContent();
			st.setStatKey("cpu|demandGhz");
			st.setData(new double[] { RoundTo2Decimals(cpuStats.get(0)), RoundTo2Decimals(cpuStats.get(1)), RoundTo2Decimals(cpuStats.get(2)) });
			st.setTimestamps(new long[] { timestamps.get(0), timestamps.get(1), timestamps.get(2) });

			StatContent st1 = new StatContent();
			st1.setStatKey("net|Secs");
			
			st1.setData(new double[] { netStats.get(0), netStats.get(1), netStats.get(2) });
			st1.setTimestamps(new long[] { timestamps.get(0), timestamps.get(1), timestamps.get(2) });

			StatContent st2 = new StatContent();
			st2.setStatKey("mem|Mhz");
			
			st2.setData(new double[] { memStats.get(0), memStats.get(1), memStats.get(2) });
			st2.setTimestamps(new long[] { timestamps.get(0), timestamps.get(1), timestamps.get(2) });

			sts = new StatContents(Arrays.asList(st, st1, st2));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new JSONObject(sts).toString().replaceFirst("statContents", "stat-content");
	}
	
	private List<Double> getMemStat()
	{
		List<Double> stats = new ArrayList<Double>();
		for(String stat : lstDockerStat)
		{
			JSONObject result = new JSONObject(stat);
			int memStat = (int) result.getJSONObject("memory_stats").get(
					"max_usage");
			double val1 = memStat;
			stats.add(val1);
		}
		return stats;
	}
	
	private List<Double> getNetStat()
	{
		List<Double> stats = new ArrayList<Double>();
		for(String stat : lstDockerStat)
		{
			JSONObject result = new JSONObject(stat);
			int netStat = (int) result.getJSONObject("network").get(
					"rx_packets");
			double val = netStat;
			stats.add(val);
		}
		return stats;
	}
	
	private List<Double> getCpuStat()
	{
		List<Double> stats = new ArrayList<Double>();
		for(String stat : lstDockerStat)
		{
			JSONObject result = new JSONObject(stat);
			int cpuStat = (int) result.getJSONObject("cpu_stats")
					.getJSONObject("cpu_usage").get("total_usage");
			stats.add(new Double (cpuStat));
		}
		return stats;
	}
	
	private List<Long> getTimestamps()
	{
		List<Long> timestamps = new ArrayList<Long>();
		for(String stat : lstDockerStat)
		{
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			JSONObject result = new JSONObject(stat);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			
			Date dt=null;
			try {
				dt = new Date();//sdf.parse(result.getString("read"));
				//System.out.println(result.getString("read") + " -- "+dt);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			long milli = dt.getTime();
			//System.out.println(dt +" .. "+milli);
			timestamps.add(date.getTime());
		}
		return timestamps;
	}

}
