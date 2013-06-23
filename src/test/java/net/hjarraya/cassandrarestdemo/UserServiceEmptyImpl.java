package net.hjarraya.cassandrarestdemo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.hjarraya.cassandrarestdemo.model.User;
import net.hjarraya.cassandrarestdemo.service.UserService;

public class UserServiceEmptyImpl implements UserService {
	private Map<String, User> users = new HashMap<String, User>();

	@Override
	public boolean addUser(User user) {
		users.put(user.getId(), user);
		return true;
	}

	@Override
	public User getUser(String id) {
		return users.get(id);
	}

	@Override
	public boolean updateUser(User user) {
		users.put(user.getId(), user);
		return true;
	}

	@Override
	public boolean removeUser(User user) {
		users.remove(user.getId());
		return true;
	}

	@Override
	public Collection<User> getUsersByFirstName(String firstName) {
		return users.values();
	}

	@Override
	public Collection<User> getUsersByLastName(String lastName) {
		return users.values();
	}

	@Override
	public Collection<User> getUsersByEmailDomain(String emaildomain) {
		return users.values();
	}

	@Override
	public boolean addEmail(User user, String email) {
		user.addEmail(email);
		users.put(user.getId(), user);
		return true;
	}

	@Override
	public boolean addPhoneNumer(User user, String phontNumber) {
		user.addPhone(phontNumber);
		users.put(user.getId(), user);
		return true;
	}
}
