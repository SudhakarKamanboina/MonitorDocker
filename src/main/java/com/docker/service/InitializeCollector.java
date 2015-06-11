package com.docker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitializeCollector {

	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		List<String> containerUrls = new ArrayList<String>();
		containerUrls.add("http://192.168.59.103:2375/v1.18/containers/nodejs/stats");
		containerUrls.add("http://10.20.132.32:4243/v1.18/containers/mysql_vm1/stats");
		containerUrls.add("http://10.20.132.247:4243/v1.18/containers/mysql_vm2/stats");
		
		for(String url : containerUrls)
		{
			executor.submit(new CollectDockerStats(url));
		}
	}

}
