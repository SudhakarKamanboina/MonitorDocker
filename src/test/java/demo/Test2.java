package demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.docker.service.ProcessStats;

public class Test2 {

	public static void main(String[] args) throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		URL docker = new URL("http://192.168.59.103:2375/v1.18/containers/nodejs/stats");
        URLConnection yc = docker.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine;
        boolean delegate=true;
        Future<Boolean> result=null;
        while ((inputLine = in.readLine()) != null) 
        {
            /*if(delegate)
            {
            	 result = executor.submit(new ProcessStats(inputLine));
            }
            delegate = result.isDone();*/
            System.out.println(delegate);
        }
        in.close();
        executor.shutdown();
        
        
	}

}
