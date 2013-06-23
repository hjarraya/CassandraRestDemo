package net.hjarraya.cassandrarestdemo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.hjarraya.cassandrarestdemo.model.User;
import net.hjarraya.cassandrarestdemo.service.UserService;

import org.apache.log4j.Logger;

public class UserServiceEmptyImpl implements UserService {
	private static final Logger logger = Logger.getLogger(UserServiceEmptyImpl.class);
	private Map<String, User> users = new HashMap<String, User>();

	@Override
	public boolean addUser(User user) {
		logger.info("[add User] Current users map size " + users.values().size());
		users.put(user.getId(), user);
		return true;
	}

	@Override
	public User getUser(String id) {
		logger.info("[get User] Current users map size " + users.values().size());
		return users.get(id);
	}

	@Override
	public boolean updateUser(User user) {
		logger.info("[update User] Current users map size " + users.values().size());
		users.put(user.getId(), user);
		return true;
	}

	@Override
	public boolean removeUser(User user) {
		logger.info("[remove User] Current users map size " + users.values().size());
		users.remove(user.getId());
		return true;
	}

	@Override
	public Collection<User> getUsersByFirstName(String firstName) {
		logger.info("Current users map size " + users.values().size());
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
	public boolean addPhoneNumber(User user, String phontNumber) {
		user.addPhone(phontNumber);
		users.put(user.getId(), user);
		return true;
	}
}
