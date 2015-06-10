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


public class CollectDockerStats {
	
	final static Logger logger = Logger.getLogger(CollectDockerStats.class);
	
	public static void main(String[] args) throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		logger.error("testing");
		
		//URL docker = new URL("http://192.168.59.103:2375/v1.18/containers/nodejs/stats");
		URL docker = new URL("http://10.20.132.56:4243/v1.18/containers/mysql_vm2/stats");
        URLConnection yc = docker.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine;
        boolean delegate=true;
        Future<Boolean> result=null;
        List<String> stats = new ArrayList<String>();
        while ((inputLine = in.readLine()) != null) 
        {
            System.out.println(inputLine);
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
        executor.shutdown();
	}

}
