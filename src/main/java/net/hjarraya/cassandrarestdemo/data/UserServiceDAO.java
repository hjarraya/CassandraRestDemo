package net.hjarraya.cassandrarestdemo.data;

import java.util.List;

import net.hjarraya.cassandrarestdemo.model.User;

public interface UserServiceDAO {
	public boolean addUser(User user);

	public User getUser(String id);

	public boolean updateUser(User user);

	public boolean removeUser(User uesr);

	public List<User> getUsersByFirstName(String firstName);

	public List<User> getUsersByLastName(String lastName);

	public List<User> getUsersByEmail(String email);

	public boolean addEmail(String id, String email);

	public boolean addPhoneNumer(String id, String phontNumber);
}
