package net.hjarraya.cassandrarestdemo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	public static Collection<User> createUsers() {
		List<User> users = new ArrayList<User>();
		int j = 0;
		for (int i = 0; i < 100; i++) {
			User user = createUser();
			user.setId("" + i);
			if (i % 2 == 0) {
				user.setFirstName(user.getFirstName() + j);
				user.setLastName(user.getFirstName() + j);
				user.addEmail("john.doe@gmail" + j + ".com");
			}
			if (i % 10 == 0)
				j++;
			users.add(user);
		}
		return users;
	}
}
