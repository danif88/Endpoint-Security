package endpoint.utils;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import endpoint.security.controllers.Application;
import endpoint.security.response.AppResponse;
/**
 * 
 * @author danielaahumada
 * Class that manage the request to the enpoint
 */

public class GSPWrapper {
	
	public static String doGET(String graphName, String accept) throws Exception {
		String queryURL = Config.fusekiDataURL + "?graph=" + graphName;
		System.out.println("Query doGet" + queryURL);
		String output = "";
		try {
			JerseyClient.client = Application.client;
			WebResource webResource = JerseyClient.client.resource(queryURL);
			ClientResponse response = webResource.accept(accept).get(ClientResponse.class);
			if (response.getStatus() != 200) {
				System.out.println("Error doGet");
			}
			output = response.getEntity(String.class);
			System.out.println("Output from Server .... \n");
			System.out.println(output);
		  } catch (Exception e) {
			  throw e;
		  }
		return output;
	}
	
	public static AppResponse doPOST(String requestBody) throws Exception {
		String queryURL;
		//String output = "";
		queryURL = Config.fusekiUpdateURL;
		System.out.println("URL Update" + queryURL);
		System.out.println("REQUEST BODY:" + requestBody);
	
		try {
			JerseyClient.client = Application.client;
			WebResource webResource = JerseyClient.client.resource(queryURL);
			MultivaluedMap formData = new MultivaluedMapImpl();
			  formData.add("update", requestBody);
			ClientResponse response = webResource.type("application/x-www-form-urlencoded").post(ClientResponse.class, formData);
			
			System.out.println(response.getStatus() + "  " + response.toString());
			if (response.getStatus() != 200 ) {
				String output = response.getEntity(String.class);
				return new AppResponse(2,response.getStatus() + "  " + output,"");
			}
			return new AppResponse(1,"","OK");
		  } catch (Exception e) {
			  return new AppResponse(2,e.getMessage(),"");
		  }
	}
	
	public static boolean doDELETE(String graphName) throws Exception{
		String queryURL = Config.fusekiDataURL + "?graph=" + graphName;
		System.out.println("doDelete queryURL=" + queryURL);
		
		try {
			JerseyClient.client = Application.client;
			System.out.println("doDelete queryURL=" + queryURL + " " + JerseyClient.client);
			WebResource webResource = JerseyClient.client.resource(queryURL);
			System.out.println("doDelete queryURL=" + queryURL);
			ClientResponse response = webResource.delete(ClientResponse.class);
			System.out.println("doDelete queryURL=" + queryURL);
			if (response.getStatus() != 200 && response.getStatus() != 204 ) {
				System.out.println("Error doDelete " + graphName + " Error:" + response.toString());
				return false;
			}

		  } catch (Exception e) {
			  System.out.println("doDeleteException " + e.toString());
			  return false;
		  }
		return true;
	}
}
