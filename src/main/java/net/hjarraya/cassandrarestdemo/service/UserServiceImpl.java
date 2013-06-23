package net.hjarraya.cassandrarestdemo.service;

import java.util.Collection;

import net.hjarraya.cassandrarestdemo.model.User;

public class UserServiceImpl implements UserService {
	@Override
	public boolean addUser(User user) {
		System.out.println("Adding user " + user);
		return false;
	}

	@Override
	public User getUser(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateUser(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeUser(User uesr) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<User> getUsersByFirstName(String firstName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<User> getUsersByLastName(String lastName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<User> getUsersByEmailDomain(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addEmail(User user, String email) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addPhoneNumer(User user, String phontNumber) {
		// TODO Auto-generated method stub
		return false;
	}
}
