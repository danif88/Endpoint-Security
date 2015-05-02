package endpoint.security.controllers;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.sun.jersey.api.client.Client;

import endpoint.security.Graphs;
import endpoint.security.Users;
import endpoint.security.session.Sessions;
import endpoint.utils.Config;


@ComponentScan
@EnableAutoConfiguration
public class Application {
	
	public static Client client;

    public static void main(String[] args) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
    	Users.init();
    	Graphs.init();
    	Sessions.init();
	Config.init();
    	initClient();
    	SpringApplication.run(Application.class, args);
    }
    
    public static void initClient() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
    	client = new Client();
    }
}
