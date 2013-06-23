package net.hjarraya.cassandrarestdemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import net.hjarraya.cassandrarestdemo.model.User;

import org.apache.log4j.BasicConfigurator;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserServiceTest {
	private static AbstractApplicationContext ctx;
	private static ObjectMapper objectMapper = new ObjectMapper();

	@BeforeClass
	public static void tearUp() {
		BasicConfigurator.configure();
		// start web server
		ctx = new ClassPathXmlApplicationContext("webservicetest-context.xml");
		ctx.registerShutdownHook();
		ctx.start();
	}

	@Test
	public void simpleClientTest() {
		RestClient client = new RestClient();
		Resource resources = client.resource("http://localhost:9001/test");
		// perform Get
		String response = resources.accept("application/json").get(String.class);
		System.out.println(response);
	}

	@Test
	public void addUserTest() throws JsonGenerationException, JsonMappingException, IOException {// -
		RestClient client = new RestClient();
		Resource resources = client.resource("http://localhost:9001/users");
		// perform Post
		User user = TestUtils.createUser();
		String userAsString = objectMapper.writeValueAsString(user);
		System.out.println(userAsString);
		String response = resources.contentType("application/json").accept("application/json")
				.post(String.class, objectMapper.writeValueAsString(user));
		System.out.println(response);
		assertTrue("true".equals(response));
	}

	@Test
	public void addRetrieveUser() throws JsonGenerationException, JsonMappingException, IOException {
		RestClient client = new RestClient();
		Resource resources = client.resource("http://localhost:9001/users");
		// perform Post
		User user = TestUtils.createUser();
		String userAsString = objectMapper.writeValueAsString(user);
		System.out.println(userAsString);
		String response = resources.contentType("application/json").accept("application/json")
				.post(String.class, objectMapper.writeValueAsString(user));
		System.out.println(response);
		assertTrue("true".equals(response));
		// retrieve user
		resources = client.resource("http://localhost:9001/users/" + user.getId());
		response = resources.accept("application/json").get(String.class);
		User actual = objectMapper.readValue(response, User.class);
		assertEquals(actual.getId(), user.getId());
	}

	@AfterClass
	public static void teadDown() {
		ctx.stop();
	}
}
