package endpoint.security.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.File;
import java.io.FileInputStream;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import endpoint.security.Graphs;
import endpoint.security.Users;
import endpoint.security.controllers.Application;
import endpoint.security.controllers.Controller;
import endpoint.security.session.Sessions;
 
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:app-context.xml")
@WebAppConfiguration
public class DeleteGraphTest {

    private MockMvc mockMvc;
    
    Application app;
    String session;
    String graphName;

    @Before
    public void setup() throws Exception {
    	Users.init();
    	Graphs.init();
    	Sessions.init();
    	Application.initClient();
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
    	graphName="http://exampletest.com/ng"+Long.toString(System.currentTimeMillis());
    	uploadGraph();
    }
 
    public void uploadGraph() throws Exception {
    			String filePath = (new File(".")).getCanonicalFile().getCanonicalFile().getCanonicalPath()
				+ "/books.ttl";

                FileInputStream fis = new FileInputStream(filePath);
                MockMultipartFile multipartFile = new MockMultipartFile("file", fis);

                MvcResult m = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload")
                        .file(multipartFile)
                        .param("name", graphName)
                        .param("session", session))
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$.status", is(1)))
                        .andExpect(jsonPath("$.error", is("")))
                        .andReturn();
                String content = m.getResponse().getContentAsString();
            	System.out.println(content + "   " + session);
    }
    
  //Test correct parameters
    @Test
    public void deleteGraphTest1() throws Exception {
    	MvcResult m = mockMvc.perform(get("/deleteGraph?session=" + session + "&graph=" + graphName))
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.error", is("")))
                .andReturn();
    	String content = m.getResponse().getContentAsString();
    	System.out.println("content1 " + content + "   " + graphName);
    	JSONObject jsonObject = new JSONObject(content);
    	assertTrue(jsonObject.get("data") != null);
    	assertTrue(jsonObject.get("data").toString().compareTo("")>0);
    }
    
    //Test invalid session
    @Test
    public void deleteGraphTest2() throws Exception {
    	MvcResult m = mockMvc.perform(get("/deleteGraph?session=" + session.substring(2) + "&graph=" + graphName))
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
    public void deleteGraphTest3() throws Exception {
    	//change rol
    	Users.setConfig("dani.role", "admin");
    	
    	MvcResult m = mockMvc.perform(get("/deleteGraph?session=" + session + "&graph=" + graphName))
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", is(2)))
                .andExpect(jsonPath("$.error", is("You don'have privileges to remove graph")))
                .andReturn();
    	String content = m.getResponse().getContentAsString();
    	System.out.println(content + "   " + session);
    	JSONObject jsonObject = new JSONObject(content);
    	assertTrue(jsonObject.get("data").toString().compareTo("")==0);
    }
    
//    //Test invalid graph
//    @Test
//    public void deleteUserToGraphTest4() throws Exception {
//    	//change rol
//    	Users.setConfig("dani.role", "superUser");
//    	
//    	//delete graph from user
//    	Users.removeConfigGraphs(session, "http://exampleTest.com/ng1");
//    	
//    	MvcResult m = mockMvc.perform(get("/deleteUserToGraph?name=vale&session=" + session + "&graph=http://exampleTest.com/ng1"))
//                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
//                .andExpect(jsonPath("$.status", is(2)))
//                .andExpect(jsonPath("$.error", is("You don'have privileges to remove user")))
//                .andReturn();
//    	String content = m.getResponse().getContentAsString();
//    	System.out.println(content + "   " + session);
//    	JSONObject jsonObject = new JSONObject(content);
//    	assertTrue(jsonObject.get("data").toString().compareTo("")==0);
//    }
}
