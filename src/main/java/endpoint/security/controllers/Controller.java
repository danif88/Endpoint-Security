package endpoint.security.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

import endpoint.security.Graphs;
import endpoint.security.User;
import endpoint.security.Users;
import endpoint.security.response.AppResponse;
import endpoint.security.session.Sessions;
import endpoint.utils.Config;
import endpoint.utils.GSPWrapper;
import enpoint.log.Logger;

/**
 * 
 * @author danielaahumada
 * Class that manage the different request coming from the Web Page.
 */

@RestController
public class Controller {
    
    @RequestMapping("/logIn")
    public AppResponse logIn(@RequestParam(value="name", required=true) String name,
    		@RequestParam(value="encrypted", required=true) String encrypted,
    		HttpServletRequest request){
    	
    		System.out.println("LOG IN:" + name + " pass:" + encrypted);
    		Logger.write("LOG IN:" + name + " pass:" + encrypted);
    		
    		AppResponse resp=User.checkUser(name, encrypted);
    		
    		System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    		Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    		
    		if(resp.getStatus()==1)
    			return Sessions.setConfig(name,request);
    		else
    			return resp;
    }
    @RequestMapping("/addUserToGraph")
    public AppResponse addUserToGraph(@RequestParam(value="name", required=true) String name,
    		@RequestParam(value="session", required=true) String session,
    		@RequestParam(value="graph", required=true) String graph,
    		HttpServletRequest request){
    	System.out.println("ADD USER TO GRAPH->graph" + graph + ",user:" + name + ",session:" + session);
    	Logger.write("ADD USER TO GRAPH->graph" + graph + ",user:" + name + ",session:" + session);
    	
    	AppResponse r = Sessions.checkSession(session, request);
    	
    	System.out.println("RESPONSE CheckSession:" + session+ " ERROR:" + r.getError());
    	Logger.write("RESPONSE CheckSession:" + session+ " ERROR:" + r.getError());
    	
    	if(r.getStatus()==1){
    		if(!User.checkIfGraphOwner(r.getData())){
    			AppResponse resp = new AppResponse(2,"You don'have privileges to add a new user","");
    			
    			System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			
    			return resp;
    		}
    		else{
    			System.out.println(Sessions.getGraphs(r.getData()));
    			if(Sessions.getGraphs(r.getData()).indexOf(graph)!=-1){
    				if(name.compareTo("")==0)
    					return new AppResponse(2,"User cannot be empty","");
    				AppResponse resp = User.addUserToGraph(r.getData(), name, graph);
    				
    				System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    				Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    				
    				return resp;
    			}
    			else{
    				AppResponse resp = new AppResponse(2,"You don'have privileges to add user","");
    				
    				System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    				Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    				
    				return resp;
    			}
    		}
    	}else{
    		System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			
    		return r;
    	}
    }
    
    @RequestMapping("/addUser")
    public AppResponse addUser(@RequestParam(value="session", required=true) String session,
    		@RequestParam(value="name", required=true) String name,
    		@RequestParam(value="encrypted", required=true) String encrypted,
    		HttpServletRequest request)
    		throws UnsupportedEncodingException {
    	System.out.println("ADD USER->name:" + name + ",session:" + session);
    	Logger.write("ADD USER->name:" + name + ",session:" + session);
    	
    	AppResponse r = Sessions.checkSession(session, request);
    	
    	System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	
    	if(r.getStatus()==1){
    		if(!User.checkIfGraphOwner(r.getData())){
    			AppResponse resp = new AppResponse(2,"You don'have privileges to add a new user","");
    			
    			System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
				Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			
    			return resp;
    		}
    		else{
    			AppResponse resp = User.addNewUser(r.getData(), name, encrypted, "user");
    			
    			System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
				Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			
    			return resp;
    		}
    	}else{
    		System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			
    		return r;
    	}
    }

    @RequestMapping("/addGraphOwnerUser")
    public AppResponse addGraphOwnerUser(@RequestParam(value="session", required=true) String session,
                @RequestParam(value="name", required=true) String name,
                @RequestParam(value="encrypted", required=true) String encrypted,
                HttpServletRequest request)
                throws UnsupportedEncodingException {
        System.out.println("ADD GRAPH OWNER USER->name:" + name + ",session:" + session);
        Logger.write("ADD GRAPH OWNER USER->name:" + name + ",session:" + session);

        AppResponse r = Sessions.checkSession(session, request);

        System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
        Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());

        if(r.getStatus()==1){
                if(!User.checkIfSuperUser(r.getData())){
                        AppResponse resp = new AppResponse(2,"You don'have privileges to add a new user","");

                        System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
                                Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());

                        return resp;
                }
                else{
                        AppResponse resp = User.addNewUser(r.getData(), name, encrypted,"graphOwner");

                        System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
                                Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());

                        return resp;
                }
        }else{
                System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
                        Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());

                return r;
        }
    }
    
    @RequestMapping("/changePass")
    public AppResponse changePass(@RequestParam(value="session", required=true) String session,
    		@RequestParam(value="encrypted", required=true) String encrypted,
    		HttpServletRequest request)
    		throws UnsupportedEncodingException {
    	System.out.println("CHANGE PASS->encrypted:" + encrypted + ",session:" + session);
    	Logger.write("CHANGE PASS->encrypted:" + encrypted + ",session:" + session);
    	
    	AppResponse r = Sessions.checkSession(session, request);
    	
    	System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	
    	if(r.getStatus()==1){
			AppResponse resp = User.changePass(r.getData(), encrypted);
			
			System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			
			return resp;
    	}else{
    		System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			
    		return r;
    	}
    }
    
    @RequestMapping("/deleteUserFromGraph")
    public AppResponse deleteUserFromGraph(@RequestParam(value="session", required=true) String session,
    		@RequestParam(value="name", required=true) String name,
    		@RequestParam(value="graph", required=true) String graph,
    		HttpServletRequest request)
    		throws UnsupportedEncodingException {
    	System.out.println("DELETE USER FROM GRAPH->graph" + graph + ",user:" + name + ",session:" + session);
    	Logger.write("DELETE USER FROM GRAPH->graph" + graph + ",user:" + name + ",session:" + session);
    	
    	AppResponse r = Sessions.checkSession(session, request);
    	
    	System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	
    	if(r.getStatus()==1){
    		if(!User.checkIfGraphOwner(r.getData())){
    			AppResponse resp = new AppResponse(2,"You don'have privileges to remove user","");
    			
    			System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
				Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			
    			return resp;
    		}
    		else{
    			if(Sessions.getGraphs(r.getData()).indexOf(graph)!=-1){
    				AppResponse resp = User.deleteUserFromGraph(r.getData(), name, graph);
    				
    				System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    				Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
        			
        			return resp;
    			}
    			else{
    				AppResponse resp = new AppResponse(2,"You don'have privileges to remove user","");
    				
    				System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    				Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
        			
        			return resp;
    			}
    		}
    	}else{
    		System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			
    		return r;
    	}
    }
    @RequestMapping("/deleteGraphOwnerUser")
    public AppResponse deleteGraphOwnerUser(@RequestParam(value="session", required=true) String session,
                @RequestParam(value="name", required=true) String name,
                HttpServletRequest request)
                throws UnsupportedEncodingException {
        System.out.println("DELETE GRAPH OWNER USER->user:" + name + ",session:" + session);
        Logger.write("DELETE GRAPH OWNER USER->user:" + name + ",session:" + session);

        AppResponse r = Sessions.checkSession(session, request);

        System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
        Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());

        if(r.getStatus()==1){
                if(!User.checkIfSuperUser(r.getData())){
                        AppResponse resp = new AppResponse(2,"You don'have privileges to remove user","");

                        System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
                                Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());

                        return resp;
                }
                else{
                                AppResponse resp = Users.deleteUser(name);

                                System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
                                Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());

                                return resp;
                }
        }else{
                System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
                        Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());

                return r;
        }
    }

    
    @RequestMapping("/deleteGraph")
    public AppResponse deleteGraph(@RequestParam(value="session", required=true) String session,
    		@RequestParam(value="graph", required=true) String graph,
    		HttpServletRequest request)
    		throws UnsupportedEncodingException {
    	System.out.println("DELETE GRAPH->graph" + graph + ",session:" + session);
    	Logger.write("DELETE GRAPH->graph" + graph + ",session:" + session);
    	
    	AppResponse r = Sessions.checkSession(session,request);
    	
    	System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	
    	if(r.getStatus()==1){
    		if(!User.checkIfGraphOwner(r.getData())){
    			AppResponse resp = new AppResponse(2,"You don'have privileges to remove graph","");
    			
    			System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
				Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			
    			return resp;
    		}
    		else{
    			if(Sessions.getGraphs(r.getData()).indexOf(graph)!=-1){
					try {
						if(GSPWrapper.doDELETE(graph)){
							Users.removeConfigGraphs(r.getData(), graph);
							AppResponse resp = new AppResponse(1,"", "Succesfull");
							
							System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
							Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			    			
			    			return resp;
						}
						else{
							AppResponse resp = new AppResponse(2,"Fails1 to remove graph", "");
							
							System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
							Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			    			
			    			return resp;
						}
					} catch (Exception e) {
						AppResponse resp = new AppResponse(2,Logger.stack2string(e), "");
						
						System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
						Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
		    			
		    			return resp;
					}	
    			}
    			else{
    				AppResponse resp = new AppResponse(2,"You don'have privileges to remove graph","");
    				
    				System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
					Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
	    			
	    			return resp;
    			}
    		}
    	}else{
    		System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			
    		return r;
    	}
    }
    
    @RequestMapping("/getUserRole")
    public AppResponse getUserRole(@RequestParam(value="session", required=true) String session,
    		HttpServletRequest request)
    		throws UnsupportedEncodingException {
    	System.out.println("GET USER ROLE->session:" + session);
    	Logger.write("GET USER ROLE->session:" + session);
    	
    	AppResponse r = Sessions.checkSession(session,request);
    	
    	System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	
    	if(r.getStatus()==1){
    		AppResponse resp = User.getUserRole(r.getData());
    		
    		System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			
			return resp;
    	}
    	else{
    		System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			
    		return r;
    	}
    }
    
    @RequestMapping("/logOut")
    public AppResponse logOut(@RequestParam(value="session", required=true) String session,
    		HttpServletRequest request)
    		throws UnsupportedEncodingException {
    	System.out.println("LOG OUT->session:" + session);
    	Logger.write("LOG OUT->session:" + session);
    	
    	AppResponse r = Sessions.checkSession(session,request);
    	
    	System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	
    	if(r.getStatus()==1){
    		Sessions.deleteSession(session);
    		
    		AppResponse resp = new AppResponse(1,"","OK");
    		
    		System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			
			return resp;
    	}
    	else{
    		System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			
    		return r;
    	}
    }
    
    @RequestMapping("/getUserGraphs")
    public AppResponse getUserGraphs(@RequestParam(value="session", required=true) String session,
    		HttpServletRequest request)
    		throws UnsupportedEncodingException {
    	System.out.println("GET USER GRAPHS->session:" + session);
    	Logger.write("GET USER GRAPHS->session:" + session);
    	
    	AppResponse r = Sessions.checkSession(session,request);
    	
    	System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	
    	if(r.getStatus()==1){
    		if(!User.checkIfGraphOwner(r.getData())){
    			AppResponse resp = new AppResponse(2,"You don'have privileges to add upload","");
    			
    			System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			
    			return resp;
    		}
    		else{
    			AppResponse resp = Graphs.getGraphs(r.getData());
    			
    			System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			
    			return resp;
    		}
    	}else{
    		System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			
    		return r;
    	}
    }
  
    @RequestMapping("/getGraphOwnerUsers")
    public AppResponse getGraphOwnerUsers(@RequestParam(value="session", required=true) String session,
                HttpServletRequest request)
                throws UnsupportedEncodingException {
        System.out.println("GET GRAPH OWNER USERS->session:" + session);
        Logger.write("GET GRAPH OWNER USERS->session:" + session);

        AppResponse r = Sessions.checkSession(session,request);

        System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
        Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());

        if(r.getStatus()==1){
                if(!User.checkIfSuperUser(r.getData())){
                        AppResponse resp = new AppResponse(2,"You don'have privileges to add upload","");

                        System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
                        Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());

                        return resp;
                }
                else{
                        AppResponse resp = Users.getUsers(r.getData());

                        System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
                        Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());

                        return resp;
                }
        }else{
                System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
                        Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());

                return r;
        }
    }
    
    @RequestMapping("/getAllUsers")
    public AppResponse getAllUsers(@RequestParam(value="session", required=true) String session,
                HttpServletRequest request)
                throws UnsupportedEncodingException {
        System.out.println("GET ALL USERS->session:" + session);
        Logger.write("GET ALL USERS->session:" + session);

        AppResponse r = Sessions.checkSession(session,request);

        System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
        Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());

        if(r.getStatus()==1){
                if(!User.checkIfSuperUser(r.getData()) && !User.checkIfGraphOwner(r.getData())){
                        AppResponse resp = new AppResponse(2,"You don'have privileges ","");

                        System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
                        Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());

                        return resp;
                }
                else{
                        AppResponse resp = Users.getAllUsers(r.getData());

                        System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
                        Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());

                        return resp;
                }
        }else{
                System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
                        Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());

                return r;
        }
    }
    @RequestMapping("/getGraphsPermited")
    public AppResponse getGraphsPermited(@RequestParam(value="session", required=true) String session,
                HttpServletRequest request)
                throws UnsupportedEncodingException {
        System.out.println("GET GRAPHS PERMITED->session:" + session);
        Logger.write("GET GRAPHS PERMITED->session:" + session);

        AppResponse r = Sessions.checkSession(session,request);

        System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
        Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());

        if(r.getStatus()==1){
                /*if(!User.checkIfSuperUser(r.getData())){
                        AppResponse resp = new AppResponse(2,"You don'have privileges ","");

                        System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
                        Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());

                        return resp;
                }
                else{*/
                        AppResponse resp = Users.getAllGraphsPermited(r.getData());

                        System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
                        Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());

                        return resp;
                //}
        }else{
                System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
                        Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());

                return r;
        }
    }
    @RequestMapping("/update")
    public AppResponse update(@RequestParam(value="session", required=true) String session,
    		@RequestParam(value="query", required=true) String query,
    		HttpServletRequest request)
    		throws Exception {
    	System.out.println("UPDATE->session:" + session + " update:" + query);
    	Logger.write("UPDATE->session:" + session + " update:" + query);
    	
    	AppResponse r = Sessions.checkSession(session,request);
    	
    	System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	
    	if(r.getStatus()==1){
    		if(Graphs.checkUpdate(r.getData(), query)){
    			AppResponse resp = GSPWrapper.doPOST(query);
    			//AppResponse resp = new AppResponse(1,"","OK");
    			
    			System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			
    			return resp;
    		}
    		else{
    			AppResponse resp = new AppResponse(2,"Fails Update - You don't have privileges","");
    			
    			System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			
    			return resp;
    		}
    	}else{
    		System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			
    		return r;
    	}
    }

    @RequestMapping("/upload")
    public AppResponse test2(@RequestParam(value="session", required=true) String session,
    		@RequestParam("name") String graph,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request){
    	Enumeration headerNames = request.getParameterNames();
    	while(headerNames.hasMoreElements()) {
    	  String headerName = (String)headerNames.nextElement();
    	  System.out.println(headerName + ":" + request.getParameter(headerName));
    	}
    	System.out.println("UPLOAD GRAPH->graph" + graph + ",session:" + session);
    	Logger.write("UPLOAD GRAPH->graph" + graph + ",session:" + session);
    	
    	AppResponse r = Sessions.checkSession(session,request);
    	
    	System.out.println("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
    	Logger.write("RESPONSE CheckSession:" + session + " ERROR:" + r.getError());
	
    	if(r.getStatus()==1){
		if(!User.checkIfSuperUser(r.getData()) && graph.compareTo("default")==0){
			AppResponse resp = new AppResponse(2,"You don'have privileges to add upload in default graph","");

                        System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
                        Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());

                        return resp;
		}
    		if(!User.checkIfGraphOwner(r.getData())){
    			AppResponse resp = new AppResponse(2,"You don'have privileges to add upload","");
    			
    			System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
    			
    			return resp;
    		}
    	}else{
    		System.out.println("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			Logger.write("RESPONSE:" + r.getData() + " ERROR:" + r.getError());
			
    		return r;
    	}
    	if(Sessions.getGraphs(r.getData()).indexOf(graph)!=-1){
    		AppResponse resp = new AppResponse(2,"Graph already exists", "");
    		
    		System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			
			return resp;
    	}
    	
    	long timeS = System.currentTimeMillis();
    	String name="file" + timeS + ".ttl";
    	
        if (file.getSize() > 0) {
        	System.out.println("Not Empty File");
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name)));
                stream.write(bytes);
                stream.close();
                final ClientConfig config=new DefaultClientConfig();
          	  final Client client=Client.create(config);
          	  final WebResource resource=client.resource(Config.fusekiUploadURL);
          	  final File fileToUpload=new File(name);
          	  final FormDataMultiPart multiPart=new FormDataMultiPart();
          	  if (fileToUpload != null) {
          	    final FileDataBodyPart fileDataBodyPart=new FileDataBodyPart("file",fileToUpload,MediaType.APPLICATION_OCTET_STREAM_TYPE);
          	    multiPart.bodyPart(fileDataBodyPart);
          	    multiPart.field("graph",graph);
          	  }
          	  final ClientResponse clientResp=resource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class,multiPart);
          	  
          	  if(clientResp.getClientResponseStatus().equals(ClientResponse.Status.OK)){
          		  String user = Sessions.getUser(r.getData());
          		  String graphs = Users.getConfig(user + ".graphs");
          		  if(graphs == null || graphs.compareTo("")==0)
          			Users.setConfig(Sessions.getUser(r.getData()) + ".graphs", graph);
          		  else
          			Users.setConfig(user + ".graphs", graphs + ";" + graph);
			  Graphs.setConfig(graph + "__admin", user);
			  User.addGraphPermited(r.getData(), graph);
          		  fileToUpload.delete();
          		  AppResponse resp = new AppResponse(1,"","Success");
          		  
					System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
					Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
					
					return resp;
          	  }else{
          		  fileToUpload.delete();
          		  String output = clientResp.getEntity(String.class); 
          		  AppResponse resp = new AppResponse(2,output,"");
          		System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + output);
				Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + output);
				
				return resp;
          	  }
          	  
            } catch (Exception e) {
            	AppResponse resp = new AppResponse(2, "You failed to upload " + name + " => " + e.getMessage(),"");
            	
            	System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
				Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
				
				return resp;
            }
        } else {
        	AppResponse resp = new AppResponse(2, "You failed to upload file because the file was empty.","");
        	
        	System.out.println("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			Logger.write("RESPONSE:" + resp.getData() + " ERROR:" + resp.getError());
			
			return resp;
        }
    }
}
