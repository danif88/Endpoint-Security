package endpoint.security;

import enpoint.log.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import endpoint.security.response.AppResponse;
import endpoint.security.session.Sessions;

/**
 * 
 * @author danielaahumada
 * Manage user.xml configuration file
 */

public class Users {
	
	private static Properties prop;
	private static String filePath;
	
	static public void init() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		prop = new Properties();
		filePath = (new File(".")).getCanonicalFile().getCanonicalFile().getCanonicalPath()
				+ "/users.xml";
		System.out.println(filePath);
		prop.loadFromXML(new FileInputStream(filePath));
	}
	
	public static String getConfig(String parameter) {
		return prop.getProperty(parameter);
	}
	
	public static void setConfig(String parameter, String value) throws IOException {
		prop.setProperty(parameter, value);
		prop.storeToXML(new FileOutputStream(filePath), "");
	}

	public static void addGraphOwnerUsers(String name) throws IOException{
		String s = getConfig("graphOwnerUsers");
                if(s==null || s.compareTo("")==0)
                        prop.setProperty("graphOwnerUsers", name);
                else{
                        if(s.indexOf(";" +name)<0 && s.indexOf(name + ";")<0 && s.compareTo(name)!=0)
                                prop.setProperty("graphOwnerUsers", s + ";" +name);
                }
                prop.storeToXML(new FileOutputStream(filePath), "");
	}

	public static void addToUsers(String name) throws IOException{
		String s = getConfig("Users");
                if(s==null || s.compareTo("")==0)
                        prop.setProperty("Users", name);
                else{
                        if(s.indexOf(";" +name)<0 && s.indexOf(name + ";")<0 && s.compareTo(name)!=0)
                                prop.setProperty("Users", s + ";" +name);
                }
                prop.storeToXML(new FileOutputStream(filePath), "");
	}
	public static boolean removeConfigGraphs(String session, String graph) throws IOException {
		String graphs = Sessions.getGraphs(session);
		List<String> graphs_a = new ArrayList<String>(Arrays.asList(graphs.split(";")));
		graphs_a.remove(graph);
		String graphs_s="";
		for(String name : graphs_a)
			graphs_s += name + ";";
		prop.setProperty(Sessions.getUser(session) + "." + "graphs", graphs_s);
		prop.storeToXML(new FileOutputStream(filePath), "");
		return true;
	}

	public static boolean removeConfigGraphsPermited(String session, String graph) throws IOException {
		String graphs = getConfig(Sessions.getUser(session) + "." + "graphs_permited");
		List<String> graphs_a = new ArrayList<String>(Arrays.asList(graphs.split(";")));
		graphs_a.remove(graph);
		String graphs_s="";
		for(String name : graphs_a)
			graphs_s += name + ";";
		prop.setProperty(Sessions.getUser(session) + "." + "graphs_permited", graphs_s);
		prop.storeToXML(new FileOutputStream(filePath), "");
		return true;
	}
	public static AppResponse getUsers(String session){
                JSONObject jsonG = new JSONObject();
                String[] users = Users.getConfig("graphOwnerUsers").split(";");
                for(int i=0; i<users.length; i++){
                        try {
                                JSONObject jsonU = new JSONObject();
				jsonG.put(users[i], jsonU);
                        } catch (JSONException e) {
                                return new AppResponse(2,"Failed to load graphs","");
                        }
                }
                return new AppResponse(1,"",jsonG.toString());
        }
	public static AppResponse getAllUsers(String session){
                JSONObject jsonG = new JSONObject();
                String[] users = Users.getConfig("Users").split(";");
                for(int i=0; i<users.length; i++){
                        try {
                                JSONObject jsonU = new JSONObject();
				jsonG.put(users[i], jsonU);
                        } catch (JSONException e) {
                                return new AppResponse(2,"Failed to load graphs","");
                        }
                }
                return new AppResponse(1,"",jsonG.toString());
        }
	public static AppResponse getAllGraphsPermited(String session){
                JSONObject jsonG = new JSONObject();
                String[] users = Users.getConfig(Sessions.getUser(session) + "." + "graphs_permited").split(";");
                for(int i=0; i<users.length; i++){
                        try {
                                JSONObject jsonU = new JSONObject();
				jsonG.put(users[i], jsonU);
                        } catch (JSONException e) {
                                return new AppResponse(2,"Failed to load graphs","");
                        }
                }
                return new AppResponse(1,"",jsonG.toString());
        }
	public static AppResponse deleteUser(String name1){
		prop.remove(name1 + ".role");
		prop.remove(name1 + ".pass");
		if(getConfig(name1 + ".graphs")!= null)
			prop.remove(name1 + ".graphs");
		try {
			prop.storeToXML(new FileOutputStream(filePath), "");
		} catch (FileNotFoundException e) {
			Logger.write("Invalid Session - Couldn't delete user" );
			return new AppResponse(2,"Couldn't delete user","");
		} catch (IOException e) {
			Logger.write("Invalid Session - Couldn't delete user" );
			return new AppResponse(2,"Couldn't delete user","");
		}
		String s = getConfig("graphOwnerUsers");
		List<String> users = new ArrayList<String>(Arrays.asList(s.split(";")));
		users.remove(name1);
		String users_s="";
		for(String name : users)
			users_s += name + ";";
		prop.setProperty("graphOwnerUsers", users_s);
		try {
			prop.storeToXML(new FileOutputStream(filePath), "");
		} catch (FileNotFoundException e) {
			Logger.write("Invalid Session - Couldn't delete user" );
			return new AppResponse(2,"Couldn't delete user","");
		} catch (IOException e) {
			Logger.write("Invalid Session - Couldn't delete user" );
			return new AppResponse(2,"Couldn't delete user","");
		}

		s = getConfig("Users");
		users = new ArrayList<String>(Arrays.asList(s.split(";")));
		users.remove(name1);
		users_s="";
		for(String name : users)
			users_s += name + ";";
		prop.setProperty("Users", users_s);
		try {
			prop.storeToXML(new FileOutputStream(filePath), "");
		} catch (FileNotFoundException e) {
			Logger.write("Invalid Session - Couldn't delete user" );
			return new AppResponse(2,"Couldn't delete user","");
		} catch (IOException e) {
			Logger.write("Invalid Session - Couldn't delete user" );
			return new AppResponse(2,"Couldn't delete user","");
		}
		return new AppResponse(1, "", "Successfull");
	}
}
