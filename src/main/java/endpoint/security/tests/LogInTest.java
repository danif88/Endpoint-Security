package endpoint.security.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import endpoint.security.Graphs;
import endpoint.security.Users;
import endpoint.security.controllers.Application;
import endpoint.security.controllers.Controller;
import endpoint.security.session.Sessions;
 
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:app-context.xml")
@WebAppConfiguration
public class LogInTest {
 
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    
    Application app;

    @Before
    public void setup() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
    	Users.init();
    	Graphs.init();
    	Sessions.init();
    	this.mockMvc = MockMvcBuilders.standaloneSetup(new Controller()).build();
    	Users.setConfig("dani.pass", "81dc9bdb52d04dc20036dbd8313ed055");
    }
 
    //Test correct parameters
    @Test
    public void logInTest1() throws Exception {
    	MvcResult m = mockMvc.perform(get("/logIn?name=dani&encrypted=81dc9bdb52d04dc20036dbd8313ed055"))
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.error", is("")))
                .andReturn();
    	String content = m.getResponse().getContentAsString();
    	JSONObject jsonObject = new JSONObject(content);
    	assertTrue(jsonObject.get("data") != null);
    	assertTrue(jsonObject.get("data").toString().compareTo("")>0);
    }
    //Test incorrect password
    @Test
    public void logInTest2() throws Exception {
    	MvcResult m = mockMvc.perform(get("/logIn?name=dani&encrypted=81dc9bdb52d04dc20036dbd8313ed0"))
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(2)))
                .andExpect(jsonPath("$.error", is("Incorrect Password")))
                .andReturn();
    	String content = m.getResponse().getContentAsString();
    	JSONObject jsonObject = new JSONObject(content);
    	assertTrue(jsonObject.get("data").toString().compareTo("")==0);
    	//System.out.println(content);
    }
    //Test incorrect user
    @Test
    public void logInTest3() throws Exception {
    	MvcResult m = mockMvc.perform(get("/logIn?name=dani1&encrypted=81dc9bdb52d04dc20036dbd8313ed055"))
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(2)))
                .andExpect(jsonPath("$.error", is("Incorrect User")))
                .andReturn();
    	String content = m.getResponse().getContentAsString();
    	JSONObject jsonObject = new JSONObject(content);
    	assertTrue(jsonObject.get("data").toString().compareTo("")==0);
    	//System.out.println(content);
    }
}
