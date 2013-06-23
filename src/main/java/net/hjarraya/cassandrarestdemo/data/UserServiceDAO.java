package net.hjarraya.cassandrarestdemo.data;

import java.util.List;

import net.hjarraya.cassandrarestdemo.model.User;

public interface UserServiceDAO {
	public User getUser(String id);

	public void updateUser(User user);

	public void removeUser(User user);

	public List<User> getUsersByFirstName(String firstName);

	public List<User> getUsersByLastName(String lastName);

	public List<User> getUsersByFirstAndLastName(String firstName, String lastName);

	public List<User> getUsersByEmail(String emailDomain);

	public void addEmail(User user, String email);

	public void addPhoneNumer(User user, String phoneNumber);
}
