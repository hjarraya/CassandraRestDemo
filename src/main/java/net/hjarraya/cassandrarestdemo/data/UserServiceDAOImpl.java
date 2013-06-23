package net.hjarraya.cassandrarestdemo.data;

import java.util.List;

import net.hjarraya.cassandrarestdemo.model.User;

public class UserServiceDAOImpl implements UserServiceDAO {
	@Override
	public boolean addUser(User user) {
		// TODO Auto-generated method stub
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
	public List<User> getUsersByFirstName(String firstName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getUsersByLastName(String lastName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getUsersByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addEmail(String id, String email) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addPhoneNumer(String id, String phontNumber) {
		// TODO Auto-generated method stub
		return false;
	}
}
