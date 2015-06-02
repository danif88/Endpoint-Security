package endpoint.security;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import endpoint.security.response.AppResponse;
import endpoint.security.session.Sessions;
import endpoint.security.User;

import java.util.Enumeration;
import java.util.Hashtable;
/**
 * 
 * @author danielaahumada
 * Manage graphs.xml configuration file.
 */

public class Graphs {
	
	private static Properties prop;
	private static String filePath;
	
	static public void init() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		prop = new Properties();
		filePath = (new File(".")).getCanonicalFile().getCanonicalFile().getCanonicalPath()
				+ "/graphs.xml";
		System.out.println(filePath);
		prop.loadFromXML(new FileInputStream(filePath));
	}
	
	public static String getConfig(String parameter) {
		return prop.getProperty(parameter);
	}
	
	public static void setConfig(String parameter, String value) throws IOException {
		String s = getConfig(parameter);
		if(s==null || s.compareTo("")==0)
			prop.setProperty(parameter, value);
		else{
			if(s.indexOf(";" + value)<0 && s.indexOf(value + ";")<0 && s.compareTo(value)!=0)
				prop.setProperty(parameter, s + ";" +value);
		}
		prop.storeToXML(new FileOutputStream(filePath), "");
	}
	
	public static boolean removeConfig(String parameter, String value) throws IOException {
		String s = getConfig(parameter);
		if(s==null)
			return false;
		else{
			List<String> users = new ArrayList<String>(Arrays.asList(s.split(";")));
			users.remove(value);
			String users_s="";
			for(String name : users)
				users_s += name + ";";
			prop.setProperty(parameter, users_s);
			prop.storeToXML(new FileOutputStream(filePath), "");
			return true;
		}
	}
	
	public static AppResponse getGraphs(String session){
		JSONObject jsonG = new JSONObject();
		if(Sessions.getGraphs(session).compareTo("")==0)
			return new AppResponse(2,"No Graphs","");
		String[] graphs = Sessions.getGraphs(session).split(";");
		for(int i=0; i<graphs.length; i++){
			try {
				JSONObject jsonU = new JSONObject();
				if(getConfig(graphs[i] + "__admin")==null){
					jsonG.put(graphs[i], jsonU);
				}else{
					String[] users = getConfig(graphs[i] + "__admin").split(";");
					for(int j=0; j<users.length; j++){
							jsonU.put("user"+j, users[j]);
					}
					jsonG.put(graphs[i], jsonU);
				}
			} catch (JSONException e) {
				return new AppResponse(2,"Failed to load graphs","");
			}
		}
		return new AppResponse(1,"",jsonG.toString());
	}
	
	public static boolean checkUpdate(String session, String update){
		String user = Sessions.getUser(session);
		String u;
		List<String> users;
		Pattern p = Pattern.compile(
	            "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" + 
	            "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" + 
	            "|mil|biz|info|mobi|name|aero|jobs|museum" + 
	            "|travel|[a-z]{2}))(:[\\d]{1,5})?" + 
	            "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" + 
	            "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
	            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" + 
	            "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
	            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" + 
	            "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");
		Matcher m= p.matcher(update);
		
		update=update.replaceAll("\t", "");
		update=update.replaceAll("\r", "");
		update=update.replaceAll("\n", "");
		update=update.replaceAll(" ", "");
		update=update.toLowerCase();
		
		boolean ans = true;
		int i=0;
		if(!User.checkIfSuperUser(session) && update.indexOf("graph") < 0){
			return false;
		}
		System.out.println("UPDATE=" + update);
		Hashtable prefixes = new Hashtable();
		int posPrefix = update.indexOf("prefix");
		while (posPrefix >= 0) {
    			int pos = update.indexOf(":", posPrefix + 1);
    			int pos2 = update.indexOf(">", posPrefix + 1);
			System.out.println("pos=" + pos + " pos2=" + pos2 + " posPrefix=" + posPrefix);
			prefixes.put(update.substring(posPrefix+6,pos),update.substring(pos+2,pos2));
    			posPrefix = update.indexOf("prefix", posPrefix + 1);
		}		
		Enumeration e = prefixes.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String uri = (String) prefixes.get(key);
			System.out.println("URI Prefix=" + uri + " key=" + key);
			int nextPosition = update.indexOf("graph" + key + ":");
			if(nextPosition > 0){
				int startUri = update.indexOf(":", nextPosition);
				int endUri = update.indexOf("{", nextPosition);
				uri = uri + update.substring(startUri+1, endUri);
				System.out.println("URI=" + uri);
				u = getConfig(uri + "__admin");
				if(u==null){
					ans = ans && false;
					continue;
				}
				users = new ArrayList<String>(Arrays.asList(u.split(";")));
				if(users.contains(user)){
					ans = ans && true;
				}
				else
					ans = ans && false;
			}
		}
		while (m.find()) {
			++i;
			String matched = m.group();
			if(update.indexOf("graph<" + matched + ">")<0) 
				continue;
			u = getConfig(matched + "__admin");
			if(u==null){
				ans = ans && false;
				continue;
			}
			users = new ArrayList<String>(Arrays.asList(u.split(";")));
			if(users.contains(user)){
				ans = ans && true;
			}
			else
				ans = ans && false;
		}
		if(i==0 && !User.checkIfSuperUser(session))
			ans = ans && false;
		return ans;
	}
	public static boolean checkUpdate2(String session, String update){
		String user = Sessions.getUser(session);
		String u;
		List<String> users;
		Pattern p = Pattern.compile(
	            "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" + 
	            "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" + 
	            "|mil|biz|info|mobi|name|aero|jobs|museum" + 
	            "|travel|[a-z]{2}))(:[\\d]{1,5})?" + 
	            "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" + 
	            "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
	            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" + 
	            "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
	            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" + 
	            "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");
		Matcher m= p.matcher(update);
		update=update.replaceAll("\t", "");
		update=update.replaceAll("\n", "");
		update=update.replaceAll(" ", "");
		update=update.toLowerCase();
		
		boolean ans = true;
		int i=0;
		int posUsing = update.indexOf("using");
		if(posUsing<0)
			posUsing=update.length();
		int posPrefix = update.indexOf("prefix");
		//List<String> prefixes = new ArrayList<String>();
		Hashtable prefixes = new Hashtable();
		while (posPrefix >= 0) {
    			posPrefix = update.indexOf("prefix", posPrefix + 1);
    			int pos = update.indexOf(":", posPrefix + 1);
    			int pos2 = update.indexOf(">", posPrefix + 1);
			prefixes.put(update.substring(posPrefix+6,pos-1),update.substring(pos+2,pos2-1));
		}		
		Enumeration e = prefixes.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String uri = (String) prefixes.get(key);
			System.out.println("URI Prefix=" + uri + " key=" + key);
			int nextPosition = update.indexOf(key + ":",update.indexOf(key + ":<"));
			if(nextPosition<posUsing){
				u = getConfig(uri + "__admin");
				if(u==null){
					ans = ans && false;
					continue;
				}
				users = new ArrayList<String>(Arrays.asList(u.split(";")));
				if(users.contains(user)){
					ans = ans && true;
				}
				else
					ans = ans && false;
			}
		}
		while (m.find()) {
			++i;
			String matched = m.group();
			int posUri = update.indexOf(matched,update.indexOf(":"+matched));
			System.out.println("URI matched=" + matched + " pos=" + posUri);
			if(posUri<posUsing){
				u = getConfig(matched + "__admin");
				if(u==null){
					ans = ans && false;
					continue;
				}
				users = new ArrayList<String>(Arrays.asList(u.split(";")));
				if(users.contains(user)){
					ans = ans && true;
				}
				else
					ans = ans && false;
			}
		}
		if(i==0 && !User.checkIfSuperUser(session))
			ans = ans && false;
		return ans;
	}

}
