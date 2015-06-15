package com.docker.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
		String jsonString = extractDockerStats();
		pushStats(jsonString);
		return true;
	}

	private void pushStats(String jsonString) {
		try
		{
			System.out.println("Inside push");
			System.out.println(jsonString);
			HttpRequest httpRequest = HttpRequest.post("https://10.20.133.250/suite-api//api/resources/d0d969ca-c8cf-4195-a1a0-80a300efa4c4/stats");
			httpRequest.body(jsonString);
			httpRequest.contentType("application/json");
			httpRequest.header("X-vRealizeOps-API-use-unsupported", "true");
			httpRequest.basicAuthentication("admin", "Login@123");
			HttpResponse response = httpRequest.send();
			System.out.println("this is for container: "+response.statusCode()+" : "+response.statusPhrase());
			
			/*StatContents sts = null;
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
			System.out.println("this is for HOST: "+response1.statusCode()+" : "+response1.statusPhrase());*/
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

	/*private String ConstructJson() {
		StatContents sts = null;
		try {

			long[] timestamps = getTimestamps();
			List<Double> cpuStats = getCpuStat();
			List<Double> netStats = getNetStat();
			List<Double> memStats = getMemStat();
			
			System.out.println(cpuStats.size());

			StatContent st = new StatContent();
			st.setStatKey("cpu|total_usage");
			st.setData(new double[] { RoundTo2Decimals(cpuStats.get(0)), RoundTo2Decimals(cpuStats.get(1)), RoundTo2Decimals(cpuStats.get(2)) });
			st.setTimestamps(timestamps);

			StatContent st1 = new StatContent();
			st1.setStatKey("net|rx_packets");
			st1.setData(new double[] { netStats.get(0), netStats.get(1), netStats.get(2) });
			st1.setTimestamps(new long[] { timestamps.get(0), timestamps.get(1), timestamps.get(2) });

			StatContent st2 = new StatContent();
			st2.setStatKey("mem|max_usage");
			st2.setData(new double[] { memStats.get(0), memStats.get(1), memStats.get(2) });
			st2.setTimestamps(new long[] { timestamps.get(0), timestamps.get(1), timestamps.get(2) });

			sts = new StatContents(Arrays.asList(st, st1, st2));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new JSONObject(sts).toString().replaceFirst("statContents", "stat-content");
	}*/
	
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
		//double[] temp = Arrays.;
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
	
	private String extractDockerStats()
	{
		try
		{
		long[] readTimes = new long[lstDockerStat.size()];
		//CPU
		double[] totalUsage = new double[lstDockerStat.size()];
		//double[] perCpuUsage = new double[lstDockerStat.size()]; //TODO in JSON there is array 
		double[] usageInKernelMode = new double[lstDockerStat.size()];
		double[] usageInUserMode = new double[lstDockerStat.size()];
		
		//Network
		double[] rxBytes = new double[lstDockerStat.size()];
		double[] rxPackets = new double[lstDockerStat.size()];
		double[] txBytes = new double[lstDockerStat.size()];
		double[] txPackets = new double[lstDockerStat.size()];
		
		//Memory
		double[] usage = new double[lstDockerStat.size()];
		double[] maxUsage = new double[lstDockerStat.size()];
		double[] activeAnon = new double[lstDockerStat.size()];
		double[] activeFile = new double[lstDockerStat.size()];
		double[] caches = new double[lstDockerStat.size()];
		double[] inActiveAnon = new double[lstDockerStat.size()];
		double[] inActiveFile = new double[lstDockerStat.size()];
		double[] mappedFile = new double[lstDockerStat.size()];
		double[] pgFault = new double[lstDockerStat.size()];
		double[] pgMajFault = new double[lstDockerStat.size()];
		double[] pgpgin = new double[lstDockerStat.size()];
		double[] pgpgout = new double[lstDockerStat.size()];
		double[] rss = new double[lstDockerStat.size()];
		double[] totalActiveAnon = new double[lstDockerStat.size()];
		double[] totalActiveFile = new double[lstDockerStat.size()];
		double[] totalCache = new double[lstDockerStat.size()];
		double[] totalInActiveAnon = new double[lstDockerStat.size()];
		double[] totalInActiveFile = new double[lstDockerStat.size()];
		double[] totalMappedFile = new double[lstDockerStat.size()];
		double[] totalPgFault = new double[lstDockerStat.size()];
		double[] totalPgMajFault = new double[lstDockerStat.size()];
		double[] totalPgPgIn = new double[lstDockerStat.size()];
		double[] totalPgPgOut = new double[lstDockerStat.size()];
		double[] totalRss = new double[lstDockerStat.size()];
		
		List<StatContent> lstStatContent = new ArrayList<StatContent>();
		
		int i=0;
		for(String stat : lstDockerStat)
		{
			readTimes[i] = getReadTime(stat);
			
			totalUsage[i] = getTotalUsage(stat);
			usageInKernelMode[i] = getUsageInKernelMode(stat);
			usageInUserMode[i] = getUsageInUserMode(stat);
			
			rxBytes[i] = getNetStatVal(stat, "rx_bytes");
			rxPackets[i] = getNetStatVal(stat, "rx_packets");
			txBytes[i] = getNetStatVal(stat, "tx_bytes");
			txPackets[i] = getNetStatVal(stat, "tx_packets");
			
			usage[i] = getUsage(stat);
			maxUsage[i] = getMaxUsage(stat);
			activeAnon[i] = getStatVal(stat, "active_anon");
			activeFile[i] = getStatVal(stat, "active_file");
			caches[i] = getStatVal(stat, "cache");
			inActiveAnon[i] = getStatVal(stat, "inactive_anon");
			inActiveFile[i] = getStatVal(stat, "inactive_file");
			mappedFile[i] = getStatVal(stat, "mapped_file");
			pgFault[i] = getStatVal(stat, "pgfault");
			pgMajFault[i] = getStatVal(stat, "pgmajfault");
			pgpgin[i] = getStatVal(stat, "pgpgin");
			pgpgout[i] = getStatVal(stat, "pgpgout");
			rss[i] = getStatVal(stat, "rss");
			totalActiveAnon[i] = getStatVal(stat, "total_active_anon");
			totalActiveFile[i] = getStatVal(stat, "total_active_file");
			totalCache[i] = getStatVal(stat, "total_cache");
			totalInActiveAnon[i] = getStatVal(stat, "total_inactive_anon");
			totalInActiveFile[i] = getStatVal(stat, "total_inactive_file");
			totalMappedFile[i] = getStatVal(stat, "total_mapped_file");
			totalPgFault[i] = getStatVal(stat, "total_pgfault");
			totalPgMajFault[i] = getStatVal(stat, "total_pgmajfault");
			totalPgPgIn[i] = getStatVal(stat, "total_pgpgin");
			totalPgPgOut[i] = getStatVal(stat, "total_pgpgout");
			totalRss[i] = getStatVal(stat, "total_rss");
			
			i++;
		}
		//CPU
		lstStatContent.add(getStatContent("cpu|total_usage", totalUsage, readTimes));
		lstStatContent.add(getStatContent("cpu|usage_in_kernelmode", usageInKernelMode, readTimes));
		lstStatContent.add(getStatContent("cpu|usage_in_usermode", usageInUserMode, readTimes));
		
		//Network
		lstStatContent.add(getStatContent("net|rx_bytes", rxBytes, readTimes));
		lstStatContent.add(getStatContent("net|rx_packets", rxPackets, readTimes));
		lstStatContent.add(getStatContent("net|tx_bytes", txBytes, readTimes));
		lstStatContent.add(getStatContent("net|tx_packets", txPackets, readTimes));
		
		//Memory
		lstStatContent.add(getStatContent("mem|usage", usage, readTimes));
		lstStatContent.add(getStatContent("mem|max_usage", maxUsage, readTimes));
		lstStatContent.add(getStatContent("mem|active_anon", activeAnon, readTimes));
		lstStatContent.add(getStatContent("mem|active_file", activeFile, readTimes));
		lstStatContent.add(getStatContent("mem|cache", caches, readTimes));
		lstStatContent.add(getStatContent("mem|inactive_anon", inActiveAnon, readTimes));
		lstStatContent.add(getStatContent("mem|inactive_file", inActiveFile, readTimes));
		lstStatContent.add(getStatContent("mem|mapped_file", mappedFile, readTimes));
		lstStatContent.add(getStatContent("mem|pgfault", pgFault, readTimes));
		lstStatContent.add(getStatContent("mem|pgmajfault", pgMajFault, readTimes));
		lstStatContent.add(getStatContent("mem|pgpgin", pgpgin, readTimes));
		lstStatContent.add(getStatContent("mem|pgpgout", pgpgout, readTimes));
		lstStatContent.add(getStatContent("mem|rss", rss, readTimes));
		lstStatContent.add(getStatContent("mem|total_active_anon", totalActiveAnon, readTimes));
		lstStatContent.add(getStatContent("mem|total_active_file", totalActiveFile, readTimes));
		lstStatContent.add(getStatContent("mem|total_cache", totalCache, readTimes));
		lstStatContent.add(getStatContent("mem|total_inactive_anon", totalInActiveAnon, readTimes));
		lstStatContent.add(getStatContent("mem|total_inactive_file", totalInActiveFile, readTimes));
		lstStatContent.add(getStatContent("mem|total_mapped_file", totalMappedFile, readTimes));
		lstStatContent.add(getStatContent("mem|total_pgfault", totalPgFault, readTimes));
		lstStatContent.add(getStatContent("mem|total_pgmajfault", totalPgMajFault, readTimes));
		lstStatContent.add(getStatContent("mem|total_pgpgin", totalPgPgIn, readTimes));
		lstStatContent.add(getStatContent("mem|total_pgpgout", totalPgPgOut, readTimes));
		lstStatContent.add(getStatContent("mem|total_rss", totalRss, readTimes));
		
		
		StatContents sts =  new StatContents(lstStatContent);
		return new JSONObject(sts).toString().replaceFirst("statContents", "stat-content");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private StatContent getStatContent(String string, double[] data, long[] readTimes) {
		StatContent st = new StatContent();
		st.setStatKey(string);
		st.setData(data);
		st.setTimestamps(readTimes);
		return st;
	}

	private double getStatVal(String stat, String statKey) {
		JSONObject result = new JSONObject(stat);
		Object memStat = result.getJSONObject("memory_stats").getJSONObject(
				"stats").get(statKey);
		return convertToDouble(memStat);
	}

	private double getMaxUsage(String stat) {
		JSONObject result = new JSONObject(stat);
		Object memStat = result.getJSONObject("memory_stats").get(
				"max_usage");
		return convertToDouble(memStat);
	}

	private double getUsage(String stat) {
		JSONObject result = new JSONObject(stat);
		Object memStat =  result.getJSONObject("memory_stats").get(
				"usage");
		return convertToDouble(memStat);
	}
	
	private double getNetStatVal(String stat, String key) {
		JSONObject result = new JSONObject(stat);
		Object netStat = result.getJSONObject("network").get(
				key);
		return convertToDouble(netStat);
	}

	private double getUsageInUserMode(String stat) {
		JSONObject result = new JSONObject(stat);
		Object totalUsage = result.getJSONObject("cpu_stats")
				.getJSONObject("cpu_usage").get("usage_in_usermode");
		return convertToDouble(totalUsage);
	}

	private double getUsageInKernelMode(String stat) {
		JSONObject result = new JSONObject(stat);
		Object  totalUsage =  result.getJSONObject("cpu_stats")
				.getJSONObject("cpu_usage").get("usage_in_kernelmode");
		
		return convertToDouble(totalUsage);
	}

	private double getTotalUsage(String stat)
	{
		JSONObject result = new JSONObject(stat);
		Object totalUsage = result.getJSONObject("cpu_stats")
				.getJSONObject("cpu_usage").get("total_usage");
		
		return convertToDouble(totalUsage);
	}
	
	private long getReadTime(String stat)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		JSONObject result = new JSONObject(stat);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		Date date = new Date();
		//System.out.println(dateFormat.format(date));
		
		Date dt=null;
		try {
			dt = new Date();//sdf.parse(result.getString("read"));
			//System.out.println(result.getString("read") + " -- "+dt);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		long milli = dt.getTime();
		//System.out.println(dt +" .. "+milli);
		return milli;
	}
	
	double convertToDouble(Object obj)
	{
		double result=0;
		if(obj.getClass().isAssignableFrom(Long.class))
		{
			//System.out.println(obj);
			long val = (long) obj;
			result = val;
			//System.out.println("in long :"+result);
			//result = Double.valueOf(truncate(String.valueOf(val)));
		}
		else if(obj.getClass().isAssignableFrom(Integer.class))
		{
			int val = (int) obj;
			result = new Double(val);
			//System.out.println("in Integer");
		}
		
		return result;
		
	}
	
	String truncate(String str)
	{
		return str.substring(0, str.indexOf(".")+3);
	}
	

}
