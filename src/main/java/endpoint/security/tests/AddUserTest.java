package endpoint.security.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
public class AddUserTest {
 
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    
    Application app;
    String session;

    @Before
    public void setup() throws Exception {
    	Users.init();
    	Graphs.init();
    	Sessions.init();
    	this.mockMvc = MockMvcBuilders.standaloneSetup(new Controller()).build();
    	Users.setConfig("dani.pass", "81dc9bdb52d04dc20036dbd8313ed055");
    	Users.setConfig("dani.role", "superUser");
    	MvcResult m = mockMvc.perform(get("/logIn?name=dani&encrypted=81dc9bdb52d04dc20036dbd8313ed055"))
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.error", is("")))
                .andReturn();
    	String content = m.getResponse().getContentAsString();
    	JSONObject jsonObject = new JSONObject(content);
    	session=jsonObject.get("data").toString();
    }
 
    //Test correct parameters
    @Test
    public void addUserTest1() throws Exception {
    	MvcResult m = mockMvc.perform(get("/addUser?name="+Long.toString(System.currentTimeMillis())+"&session=" + session + "&encrypted=8892"))
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.error", is("")))
                .andReturn();
    	String content = m.getResponse().getContentAsString();
    	System.out.println(content + "   " + session);
    	JSONObject jsonObject = new JSONObject(content);
    	assertTrue(jsonObject.get("data") != null);
    	assertTrue(jsonObject.get("data").toString().compareTo("")>0);
    }
    //Test invalid session
    @Test
    public void addUserTest2() throws Exception {
    	MvcResult m = mockMvc.perform(get("/addUser?name=vale&session=" + session.substring(2) + "&encrypted=8892"))
    	        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(2)))
                .andExpect(jsonPath("$.error", is("Invalid Session")))
                .andReturn();
    	String content = m.getResponse().getContentAsString();
    	System.out.println(content + "   " + session);
    	JSONObject jsonObject = new JSONObject(content);
    	assertTrue(jsonObject.get("data").toString().compareTo("")==0);
    }
    
    //Test invalid rol
    @Test
    public void addUserTest3() throws Exception {
    	//change rol
    	Users.setConfig("dani.role", "admin");
    	
    	MvcResult m = mockMvc.perform(get("/addUser?name=vale&session=" + session + "&encrypted=8892"))
    	        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(2)))
                .andExpect(jsonPath("$.error", is("You don'have privileges to add a new user")))
                .andReturn();
    	String content = m.getResponse().getContentAsString();
    	System.out.println(content + "   " + session);
    	JSONObject jsonObject = new JSONObject(content);
    	assertTrue(jsonObject.get("data").toString().compareTo("")==0);
    }
    
    //Test user already exists
    @Test
    public void addUserTest4() throws Exception {
    	//change rol
    	Users.setConfig("dani.role", "superUser");
    	
    	MvcResult m = mockMvc.perform(get("/addUser?name=dani&session=" + session + "&encrypted=8892"))
    	        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(2)))
                .andExpect(jsonPath("$.error", is("User already exists")))
                .andReturn();
    	String content = m.getResponse().getContentAsString();
    	System.out.println(content + "   " + session);
    	JSONObject jsonObject = new JSONObject(content);
    	assertTrue(jsonObject.get("data").toString().compareTo("")==0);
    }
}
