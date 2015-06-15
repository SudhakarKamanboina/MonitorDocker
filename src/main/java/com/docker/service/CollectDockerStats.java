package com.docker.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;


public class CollectDockerStats implements Runnable {
	
	final static Logger logger = Logger.getLogger(CollectDockerStats.class);
	private String strUrl;
	
	public CollectDockerStats(String url) {
			this.strUrl = url;
	}

	@Override
	public void run() {
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		try
		{
			DockerProperties dp = new DockerProperties(strUrl);
			dp.collectProperties();
			logger.debug("Thread for url: "+strUrl);
			URL docker = new URL(strUrl);
	        URLConnection yc = docker.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        String inputLine;
	        boolean delegate=true;
	        Future<Boolean> result=null;
	        List<String> stats = new ArrayList<String>();
	        while ((inputLine = in.readLine()) != null) 
	        {
	            System.out.println(" --- "+inputLine);
	        	stats.add(inputLine);
	        	if(delegate && stats.size() ==3)
	            {
	        		result = executor.submit(new ProcessStats(stats));
	            }
	            if(null != result)
	            {
	            	delegate = result.isDone();
	            	if(delegate)
	            	{
	            		stats.removeAll(stats);
	            		result = null;
	            	}
	            }
	            
	        }
	        in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        executor.shutdown();
	}

}
