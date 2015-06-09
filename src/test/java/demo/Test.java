package demo;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Test {

	public static void main(String[] args) {
		/*HttpRequest httpRequest = HttpRequest.get("http://192.168.59.103:2375/v1.18/containers/json");//http://192.168.59.103:2375/v1.18/containers/json http://192.168.59.103:2375/v1.18/containers/nodejs/stats
		httpRequest.connectionKeepAlive(true);
		httpRequest.accept("application/octet-stream");
	    HttpResponse response = httpRequest.send();*/

	    //System.out.println(response);
		/*Model model = new Model();
		model.setRead("2015-06-07T17:02:07.59692181Z");
		ObjectMapper mapper = new ObjectMapper();
		try {
			String strJson = mapper.writeValueAsString(model);
			System.out.println(strJson);
			
			try {
				model = mapper.readValue(strJson, Model.class);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	}
}
