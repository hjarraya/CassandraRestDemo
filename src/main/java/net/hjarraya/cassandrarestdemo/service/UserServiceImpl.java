package net.hjarraya.cassandrarestdemo.service;

import java.util.Collection;
import java.util.Collections;

import net.hjarraya.cassandrarestdemo.data.UserServiceDAO;
import net.hjarraya.cassandrarestdemo.model.User;

import org.apache.log4j.Logger;

public class UserServiceImpl implements UserService {
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);
	private final UserServiceDAO userServiceDAO;

	public UserServiceImpl(UserServiceDAO userServiceDAO) {
		this.userServiceDAO = userServiceDAO;
	}

	@Override
	public boolean addUser(User user) {
		logger.info("Adding user " + user);
		try {
			userServiceDAO.updateUser(user);
		} catch (Exception e) {
			logger.error("Unable to add user " + user, e);
			return false;
		}
		return true;
	}

	@Override
	public User getUser(String id) {
		logger.info("Get user by id " + id);
		try {
			return userServiceDAO.getUser(id);
		} catch (Exception e) {
			logger.error("Unable to retrieve user " + id, e);
		}
		return new User();
	}

	@Override
	public boolean updateUser(User user) {
		logger.info("Update user " + user);
		try {
			userServiceDAO.updateUser(user);
		} catch (Exception e) {
			logger.error("Unable to Update user " + user, e);
			return false;
		}
		return true;
	}

	@Override
	public boolean removeUser(User user) {
		logger.info("Deleting user " + user);
		try {
			userServiceDAO.removeUser(user);
		} catch (Exception e) {
			logger.error("Unable to delete user " + user, e);
			return false;
		}
		return true;
	}

	@Override
	public Collection<User> getUsersByFirstName(String firstName) {
		logger.info("Getting users by first name " + firstName);
		try {
			return userServiceDAO.getUsersByFirstName(firstName);
		} catch (Exception e) {
			logger.error("Unable to get users by first name " + firstName, e);
			return Collections.EMPTY_SET;
		}
	}

	@Override
	public Collection<User> getUsersByLastName(String lastName) {
		logger.info("Getting users by last name " + lastName);
		try {
			return userServiceDAO.getUsersByLastName(lastName);
		} catch (Exception e) {
			logger.error("Unable to get users by last name " + lastName, e);
			return Collections.EMPTY_SET;
		}
	}

	@Override
	public Collection<User> getUsersByEmailDomain(String emaildomain) {
		logger.info("Getting users by email domain " + emaildomain);
		try {
			return userServiceDAO.getUsersByEmail(emaildomain);
		} catch (Exception e) {
			logger.error("Unable to get users by email domain" + emaildomain, e);
			return Collections.EMPTY_SET;
		}
	}

	@Override
	public boolean addEmail(User user, String email) {
		logger.info("Adding email " + email + " to  user " + user);
		try {
			userServiceDAO.addEmail(user, email);
		} catch (Exception e) {
			logger.error("Unable to add email " + email + " to user " + user, e);
			return false;
		}
		return true;
	}

	@Override
	public boolean addPhoneNumber(User user, String phone) {
		logger.info("Adding phone number " + phone + " to  user " + user);
		try {
			userServiceDAO.addPhoneNumber(user, phone);
		} catch (Exception e) {
			logger.error("Unable to add phone number " + phone + " to user " + user, e);
			return false;
		}
		return true;
	}
}
