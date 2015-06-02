package endpoint.security;

import java.io.IOException;

import endpoint.security.response.AppResponse;
import endpoint.security.session.Sessions;
import endpoint.security.Users;

/**
 * 
 * @author danielaahumada
 * Class that manage Users
 */

public class User {

    public static AppResponse addNewUser(String session, String name, String pass, String role){
    	if(checkIfExists(name).compareTo("OK")!=0)
    		return new AppResponse(2,"User already exists","");
    	if(!checkIfGraphOwner(session))
    		return new AppResponse(2,"You don't have privileges to add a user","");
    	try {
			Users.setConfig(name + ".pass", pass);
			Users.setConfig(name + ".role", role);
			Users.setConfig(name + ".graphs", role);
			Users.setConfig(name + ".graphs_permited", role);
			Users.addToUsers(name);
		} catch (IOException e) {
			return new AppResponse(2,e.getMessage(),"");
		}
	if(role.compareTo("graphOwner")==0){
		try{
		Users.addGraphOwnerUsers(name);
		} catch (IOException e) {
                        return new AppResponse(2,e.getMessage(),"");
                }
	}
    	return new AppResponse(1,"","User created");
    }
    
    public static AppResponse checkUser(String name, String pass){
    	String s = Users.getConfig(name + ".pass");
    	if(s==null)
    		return new AppResponse(2,"Incorrect User","");
    	if(s.compareTo(pass)==0)
    		return new AppResponse(1,"","OK");
    	else
    		return new AppResponse(2,"Incorrect Password","");
    }
    
    public static boolean checkIfGraphOwner(String session){
    	String role = Sessions.getRole(session);
    	if(role!=null)
    		return role.compareTo("superUser")==0 || role.compareTo("graphOwner")==0;
    	else
    		return false;
    }

    public static boolean checkIfSuperUser(String session){
        String role = Sessions.getRole(session);
        if(role!=null)
                return role.compareTo("superUser")==0;
        else
                return false;
    }
    
    public static String checkIfExists(String name){
    	String user = Users.getConfig(name + ".pass");
    	if(user==null)
    		return "OK";
    	else
    		return "User already exists";
    }

	public static AppResponse addGraphOwner(String session, String graph) {
		String user = Sessions.getConfig(session);
		String graphs = Users.getConfig(user + ".graphs");
		//System.out.println("index + " + graphs.indexOf(graph));
		if(graphs.indexOf(graph)>=0)
			return new AppResponse(1,"","OK");
		System.out.println("graphs" + graphs+"#");
		try {
			if(graphs==null || graphs.compareTo("")==0)
					Users.setConfig(user + ".graphs", graph);
			else
	    		Users.setConfig(user + ".graphs", graphs + ";" + graph);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AppResponse(1,"","OK");
	}

	public static AppResponse addGraphPermited(String session, String graph) {
		String user = Sessions.getConfig(session);
		String graphs = Users.getConfig(user + ".graphs_permited");
		if(graphs.indexOf(graph)>=0)
			return new AppResponse(1,"","OK");
		System.out.println("graphs" + graphs+"#");
		try {
			if(graphs==null || graphs.compareTo("")==0)
					Users.setConfig(user + ".graphs_permited", graph);
			else
	    		Users.setConfig(user + ".graphs_permited", graphs + ";" + graph);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AppResponse(1,"","OK");
	}
	public static AppResponse getUserRole(String session) {
		String user = Sessions.getConfig(session);
		return new AppResponse(1, "",Users.getConfig(user + ".role"));
	}

	public static AppResponse deleteUserFromGraph(String session, String name, String graph) {
		try {
			if(Graphs.removeConfig(graph + "__admin", name)){
				Users.removeConfigGraphsPermited(session, graph);				
				return new AppResponse(1, "", "Succesfull");
			}
			else
				return new AppResponse(2, "Fails to remove user from graph", "");
		} catch (IOException e) {
			return new AppResponse(2, "Fails to remove user from graph", "");
		}
	}

	public static AppResponse addUserToGraph(String session, String name,
			String graph) {
		try {
			if(Users.getConfig(name + ".pass") != null){
				Graphs.setConfig(graph + "__admin", name);
				addGraphPermited(session, graph);
				return new AppResponse(1, "", "Succesfull");
			}
			else
				return new AppResponse(2, "User doesn't exist", "");
		} catch (IOException e) {
			return new AppResponse(2, "Fails to add user from graph", "");
		}
	}

	public static AppResponse changePass(String session, String encrypted) {
		String name = Sessions.getUser(session);
		try {
			Users.setConfig(name + ".pass", encrypted);
		} catch (IOException e) {
			return new AppResponse(2,"","Couldn't change password");
		}
		return new AppResponse(1,"","Succesfull");
	}
}
