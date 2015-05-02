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
public class UploadTest {

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
    public void uploadTest1() throws Exception {
    			String filePath = (new File(".")).getCanonicalFile().getCanonicalFile().getCanonicalPath()
				+ "/books.ttl";

                FileInputStream fis = new FileInputStream(filePath);
                MockMultipartFile multipartFile = new MockMultipartFile("file", fis);

                MvcResult m = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload")
                        .file(multipartFile)
                        .param("name", "http://exampletest.com/ng"+Long.toString(System.currentTimeMillis()))
                        .param("session", session))
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$.status", is(1)))
                        .andExpect(jsonPath("$.error", is("")))
                        .andReturn();
                String content = m.getResponse().getContentAsString();
            	JSONObject jsonObject = new JSONObject(content);
            	assertTrue(jsonObject.get("data") != null);
            	assertTrue(jsonObject.get("data").toString().compareTo("")>0);
    }
    
    //Test empty file
    @Test
    public void uploadTest2() throws Exception {
    			String filePath = (new File(".")).getCanonicalFile().getCanonicalFile().getCanonicalPath()
				+ "/books1.ttl";

                FileInputStream fis = new FileInputStream(filePath);
                MockMultipartFile multipartFile = new MockMultipartFile("file", fis);

                MvcResult m = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload")
                        .file(multipartFile)
                        .param("name", "http://exampletest.com/ng"+Long.toString(System.currentTimeMillis()))
                        .param("session", session))
                        .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$.status", is(2)))
                        .andExpect(jsonPath("$.error", is("You failed to upload file because the file was empty.")))
                        .andReturn();
                String content = m.getResponse().getContentAsString();
                System.out.println(content);
            	JSONObject jsonObject = new JSONObject(content);
            	assertTrue(jsonObject.get("data") != null);
            	assertTrue(jsonObject.get("data").toString().compareTo("")==0);
    }
}
