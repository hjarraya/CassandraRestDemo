package net.hjarraya.cassandrarestdemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.hjarraya.cassandrarestdemo.model.User;
import net.hjarraya.cassandrarestdemo.util.Utils;

import org.apache.log4j.BasicConfigurator;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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

	public void addUserTest() throws JsonGenerationException, JsonMappingException, IOException {
		RestClient client = new RestClient();
		Resource resources = client.resource("http://localhost:9001/users/add");
		// perform Post
		User user = TestUtils.createUser();
		String userAsString = objectMapper.writeValueAsString(user);
		System.out.println(userAsString);
		String response = resources.contentType("application/json").accept("application/json")
				.post(String.class, objectMapper.writeValueAsString(user));
		System.out.println(response);
		assertTrue("true".equals(response));
	}

	public void addMultipleUserTest() throws JsonGenerationException, JsonMappingException, IOException {
		Collection<User> users = TestUtils.createUsers();
		RestClient client = new RestClient();
		for (User user : users) {
			Resource resources = client.resource("http://localhost:9001/users/add");
			// perform Post
			String userAsString = objectMapper.writeValueAsString(user);
			System.out.println(userAsString);
			String response = resources.contentType("application/json").accept("application/json")
					.post(String.class, objectMapper.writeValueAsString(user));
			System.out.println(response);
			assertTrue("true".equals(response));
		}
	}

	@Test
	public void addRetrieveUser() throws JsonGenerationException, JsonMappingException, IOException {
		addUserTest();
		User user = TestUtils.createUser();
		// retrieve user
		RestClient client = new RestClient();
		Resource resources = client.resource("http://localhost:9001/users/" + user.getId());
		String response = resources.accept("application/json").get(String.class);
		User actual = objectMapper.readValue(response, User.class);
		assertEquals(actual.getId(), user.getId());
	}

	@Test
	public void updateUserTest() throws JsonGenerationException, JsonMappingException, IOException {
		addUserTest();
		User user = TestUtils.createUser();
		user.setFirstName("Barry");
		// update client
		RestClient client = new RestClient();
		Resource resources = client.resource("http://localhost:9001/users/update");
		String response = resources.contentType("application/json").accept("application/json")
				.post(String.class, objectMapper.writeValueAsString(user));
		assertTrue("true".equals(response));
		// check that update did happen
		User actual = getUser(user.getId());
		assertEquals(actual.getFirstName(), user.getFirstName());
	}

	@Test
	public void deleteUserTest() throws JsonGenerationException, JsonMappingException, IOException {
		addUserTest();
		User user = TestUtils.createUser();
		RestClient client = new RestClient();
		Resource resources = client.resource("http://localhost:9001/users/remove");
		String response = resources.contentType("application/json").accept("application/json")
				.post(String.class, objectMapper.writeValueAsString(user));
		assertTrue("true".equals(response));
		User actual = getUser(user.getId());
		assertTrue(actual.getId() == null);
	}

	public User getUser(String id) {
		try {
			RestClient client = new RestClient();
			Resource resources = client.resource("http://localhost:9001/users/" + id);
			String response = resources.accept("application/json").get(String.class);
			User actual = objectMapper.readValue(response, User.class);
			return actual;
		} catch (Exception ex) {
			return new User();
		}
	}

	@Test
	public void getUsersByFirstName() throws JsonGenerationException, JsonMappingException, IOException {
		addMultipleUserTest();
		User user = TestUtils.createUser();
		System.out.println(objectMapper.writeValueAsString(user.getFirstName()));
		RestClient client = new RestClient();
		Resource resources = client.resource("http://localhost:9001/users/firstname/" + user.getFirstName());
		String response = resources.contentType("application/json").accept("application/json")
				.get(String.class);
		System.out.println(response);
		List<User> actual = objectMapper.readValue(response, new TypeReference<List<User>>() {
		});
		System.out.println("retreive it object " + actual.size());
		assertTrue(actual.size() != 0);
	}

	@Test
	public void getUsersByLastName() throws JsonGenerationException, JsonMappingException, IOException {
		addMultipleUserTest();
		User user = TestUtils.createUser();
		System.out.println(objectMapper.writeValueAsString(user.getLastName()));
		RestClient client = new RestClient();
		Resource resources = client.resource("http://localhost:9001/users/lastname/" + user.getLastName());
		String response = resources.contentType("application/json").accept("application/json")
				.get(String.class);
		System.out.println(response);
		List<User> actual = objectMapper.readValue(response, new TypeReference<List<User>>() {
		});
		System.out.println("retreive it object " + actual.size());
		assertTrue(actual.size() != 0);
	}

	@Test
	public void getEmailDomain() throws JsonGenerationException, JsonMappingException, IOException {
		addMultipleUserTest();
		User user = TestUtils.createUser();
		String emailDomain = Utils.getDomainFromEmail(user.getEmails().iterator().next());
		RestClient client = new RestClient();
		Resource resources = client.resource("http://localhost:9001/users/emaildomain/" + emailDomain);
		String response = resources.contentType("application/json").accept("application/json")
				.get(String.class);
		System.out.println(response);
		List<User> actual = objectMapper.readValue(response, new TypeReference<List<User>>() {
		});
		System.out.println("retreive it object " + actual.size());
		assertTrue(actual.size() != 0);
	}

	@Test
	public void addEmailTest() throws JsonGenerationException, JsonMappingException, IOException {
		addUserTest();
		User user = TestUtils.createUser();
		RestClient client = new RestClient();
		String emailToAdd = "john.doe@hotmail.com";
		user.addEmail(emailToAdd);
		Resource resources = client.resource("http://localhost:9001/users/email/" + emailToAdd);
		String response = resources.contentType("application/json").accept("application/json")
				.post(String.class, objectMapper.writeValueAsString(user));
		assertTrue("true".equals(response));
		User actual = getUser(user.getId());
		assertEquals(user.getEmails(), actual.getEmails());
	}

	@Test
	public void addPhoneNumberTest() throws JsonGenerationException, JsonMappingException, IOException {
		addUserTest();
		User user = TestUtils.createUser();
		RestClient client = new RestClient();
		String phoneToAdd = "555-555-555";
		user.addPhone(phoneToAdd);
		Resource resources = client.resource("http://localhost:9001/users/phone/" + phoneToAdd);
		String response = resources.contentType("application/json").accept("application/json")
				.post(String.class, objectMapper.writeValueAsString(user));
		assertTrue("true".equals(response));
		User actual = getUser(user.getId());
		assertEquals(user.getPhones(), actual.getPhones());
	}

	@AfterClass
	public static void teadDown() {
		ctx.stop();
	}
}
