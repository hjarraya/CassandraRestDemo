package net.hjarraya.cassandrarestdemo.service;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.hjarraya.cassandrarestdemo.model.User;

public interface UserService {
	@GET
	@Path("/test")
	@Produces({ "application/json", "text/xml" })
	public String returnHelloMessage();

	@POST
	@Path("/users")
	@Consumes({ "application/json", "text/xml" })
	public boolean addUser(User user);

	@GET
	@Path("/users/{id}")
	@Produces({ "application/json", "text/xml" })
	public User getUser(@PathParam("id") String id);

	public boolean updateUser(User user);

	public boolean removeUser(User user);

	public Collection<User> getUsersByFirstName(String firstName);

	public Collection<User> getUsersByLastName(String lastName);

	public Collection<User> getUsersByEmailDomain(String emaildomain);

	public boolean addEmail(User user, String email);

	public boolean addPhoneNumer(User user, String phontNumber);
}
