package net.hjarraya.cassandrarestdemo;

import net.hjarraya.cassandrarestdemo.model.User;

public class TestUtils {
	public static User createUser() {
		User user = new User();
		user.setId("1");
		user.setFirstName("john");
		user.setLastName("Doe");
		user.addEmail("john.doe@gmail.com");
		user.addPhone("777-777-777");
		return user;
	}
}
