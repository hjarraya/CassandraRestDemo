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
	@POST
	@Path("/users/add")
	@Consumes({ "application/json", "text/xml" })
	public boolean addUser(User user);

	@GET
	@Path("/users/{id}")
	@Produces({ "application/json", "text/xml" })
	public User getUser(@PathParam("id") String id);

	@POST
	@Path("/users/update")
	@Consumes({ "application/json", "text/xml" })
	public boolean updateUser(User user);

	@POST
	@Path("/users/remove")
	@Consumes({ "application/json", "text/xml" })
	public boolean removeUser(User user);

	@GET
	@Path("/users/firstname/{firstname}")
	@Produces({ "application/json", "text/xml" })
	public Collection<User> getUsersByFirstName(@PathParam("firstname") String firstName);

	@GET
	@Path("/users/lastname/{lastname}")
	@Produces({ "application/json", "text/xml" })
	public Collection<User> getUsersByLastName(@PathParam("lastname") String lastName);

	@GET
	@Path("/users/emaildomain/{emaildomain}")
	@Produces({ "application/json", "text/xml" })
	public Collection<User> getUsersByEmailDomain(@PathParam("emaildomain") String emaildomain);

	@GET
	@Path("/users/update/{email}")
	@Produces({ "application/json", "text/xml" })
	public boolean addEmail(User user, @PathParam("email") String email);

	@GET
	@Path("/users/update/{phone}")
	@Produces({ "application/json", "text/xml" })
	public boolean addPhoneNumber(User user, @PathParam("phone") String phontNumber);
}
