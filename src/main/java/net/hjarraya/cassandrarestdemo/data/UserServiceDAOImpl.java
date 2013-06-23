package net.hjarraya.cassandrarestdemo.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.hjarraya.cassandrarestdemo.model.User;
import net.hjarraya.cassandrarestdemo.util.Utils;

import org.apache.log4j.Logger;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class UserServiceDAOImpl implements UserServiceDAO {
	private static final Logger logger = Logger.getLogger(UserServiceDAOImpl.class);
	private static final String INSERT_USER = "UPDATE users.users SET firstName=? , lastName = ? , emails = ?, phoneNumbers = ? WHERE id =?;";
	private static final String SELECT_USER = "SELECT * FROM users.users WHERE id=?;";
	private static final String DELET_USER = "DELETE FROM users.users WHERE id=?;";
	private static final String SELECT_USER_BY_FN = "SELECT * FROM users.users WHERE firstName=?;";
	private static final String SELECT_USER_BY_LN = "SELECT * FROM users.users WHERE lastName=?;";
	private static final String SELECT_USER_BY_FN_LN = "SELECT * FROM users WHERE firstName=? AND lastName=?;";
	private static final String SELECT_USERS_BY_EMAIL = "SELECT * FROM users.emaildomain WHERE domain=?;";
	private static final String ADD_EMAIL_DOMAIN = "UPDATE users.emaildomain SET  ids = ids + ? where domain =? and firstName = ?";
	private static final String REMOVE_EMAIL_DOMAIN = "UPDATE users.emaildomain SET  ids = ids - ? where domain =? and firstName = ?";
	private static final String ADD_EMAIl = "UPDATE users.users SET emails = emails + ? WHERE id = ?;";
	private static final String ADD_PHONE = "UPDATE users.users SET phoneNumbers = phoneNumbers + ? WHERE id = ?;";
	@SuppressWarnings("unused")
	private final Cluster cluster;
	private final Session session;
	private final PreparedStatement addUpdateUserStmt;
	private final PreparedStatement addEmailDomainStmt;
	private final PreparedStatement removeEmailDomainStmt;
	private final PreparedStatement getUserStmt;
	private final PreparedStatement removeUserStmt;
	private final PreparedStatement userByFirstNameStmt;
	private final PreparedStatement userByLastNameStmt;
	private final PreparedStatement userByFirstAndLastNameStmt;
	private final PreparedStatement usersByEmailStmt;
	private final PreparedStatement addUserEmailStmt;
	private final PreparedStatement addUserPhoneNumberStmt;

	public UserServiceDAOImpl(Cluster cluster) {
		this.cluster = cluster;
		this.session = cluster.connect();
		this.addUpdateUserStmt = session.prepare(INSERT_USER);
		this.addEmailDomainStmt = session.prepare(ADD_EMAIL_DOMAIN);
		this.removeEmailDomainStmt = session.prepare(REMOVE_EMAIL_DOMAIN);
		this.getUserStmt = session.prepare(SELECT_USER);
		this.removeUserStmt = session.prepare(DELET_USER);
		this.userByFirstNameStmt = session.prepare(SELECT_USER_BY_FN);
		this.userByLastNameStmt = session.prepare(SELECT_USER_BY_LN);
		this.userByFirstAndLastNameStmt = session.prepare(SELECT_USER_BY_FN_LN);
		this.usersByEmailStmt = session.prepare(SELECT_USERS_BY_EMAIL);
		this.addUserEmailStmt = session.prepare(ADD_EMAIl);
		this.addUserPhoneNumberStmt = session.prepare(ADD_PHONE);
	}

	@Override
	public void updateUser(User user) {
		logger.info("Updating/Adding user " + user);
		// add user
		BoundStatement userBs = addUpdateUserStmt.bind(user.getFirstName(), user.getLastName(),
			user.getEmails(), user.getPhones(), user.getId());
		session.execute(userBs);
		// add email domain
		Set<String> emails = new HashSet<String>(user.getEmails());
		Set<String> alreadyInsertedDomain = new HashSet<String>();
		for (String email : emails) {
			String emailDomain = Utils.getDomainFromEmail(email);
			if (!alreadyInsertedDomain.contains(emailDomain)) {
				Set<String> idWrapper = new HashSet<String>();
				idWrapper.add(user.getId());
				BoundStatement domainBS = addEmailDomainStmt
						.bind(idWrapper, emailDomain, user.getFirstName());
				session.execute(domainBS);
				alreadyInsertedDomain.add(emailDomain);
			}
		}
	}

	@Override
	public User getUser(String id) {
		logger.info("Get user " + id);
		User user = new User();
		user.setId(id);
		BoundStatement bs = getUserStmt.bind(user.getId());
		ResultSet rest = session.execute(bs);
		List<Row> rows = rest.all();
		if (!rows.isEmpty()) {
			Row row = rows.get(0);
			user.setFirstName(row.getString("firstName"));
			user.setLastName(row.getString("lastName"));
			user.setEmails(row.getSet("emails", String.class));
			user.setPhones(row.getSet("phones", String.class));
		}
		logger.info("User retreive it " + user);
		return user;
	}

	@Override
	public void removeUser(User user) {
		logger.info("Deleting user " + user);
		// remove user
		BoundStatement deleteBS = removeUserStmt.bind(user.getId());
		session.execute(deleteBS);
		// delete emails domain
		Set<String> alreadyDeletedDomain = new HashSet<String>();
		for (String email : user.getEmails()) {
			String emailDomain = Utils.getDomainFromEmail(email);
			if (!alreadyDeletedDomain.contains(emailDomain)) {
				Set<String> idWrapper = new HashSet<String>();
				idWrapper.add(user.getId());
				BoundStatement domainBS = removeEmailDomainStmt.bind(idWrapper, emailDomain,
					user.getFirstName());
				session.execute(domainBS);
				alreadyDeletedDomain.add(emailDomain);
			}
		}
	}

	@Override
	public List<User> getUsersByFirstName(String firstName) {
		logger.info("Getting user by First Name " + firstName);
		List<User> users = new ArrayList<User>();
		BoundStatement usersBS = userByFirstNameStmt.bind(firstName);
		ResultSet rest = session.execute(usersBS);
		List<Row> rows = rest.all();
		for (Row row : rows) {
			User user = new User();
			user.setId(row.getString("id"));
			user.setFirstName(row.getString("firstName"));
			user.setLastName(row.getString("lastName"));
			user.setEmails(row.getSet("emails", String.class));
			user.setPhones(row.getSet("phones", String.class));
			users.add(user);
		}
		return users;
	}

	@Override
	public List<User> getUsersByLastName(String lastName) {
		logger.info("Getting user by Last Name " + lastName);
		List<User> users = new ArrayList<User>();
		BoundStatement usersBS = userByLastNameStmt.bind(lastName);
		ResultSet rest = session.execute(usersBS);
		List<Row> rows = rest.all();
		for (Row row : rows) {
			User user = new User();
			user.setId(row.getString("id"));
			user.setFirstName(row.getString("firstName"));
			user.setLastName(row.getString("lastName"));
			user.setEmails(row.getSet("emails", String.class));
			user.setPhones(row.getSet("phones", String.class));
			users.add(user);
		}
		return users;
	}

	@Override
	public List<User> getUsersByFirstAndLastName(String firstName, String lastName) {
		logger.info("Getting user by First & Last  Name " + firstName + " - " + lastName);
		List<User> users = new ArrayList<User>();
		BoundStatement usersBS = userByFirstAndLastNameStmt.bind(firstName, lastName);
		ResultSet rest = session.execute(usersBS);
		List<Row> rows = rest.all();
		for (Row row : rows) {
			User user = new User();
			user.setId(row.getString("id"));
			user.setFirstName(row.getString("firstName"));
			user.setLastName(row.getString("lastName"));
			user.setEmails(row.getSet("emails", String.class));
			user.setPhones(row.getSet("phones", String.class));
			users.add(user);
		}
		return users;
	}

	@Override
	public List<User> getUsersByEmail(String emailDomain) {
		logger.info("Getting user by email domain  " + emailDomain);
		List<User> users = new ArrayList<User>();
		// get all ids
		BoundStatement idsBS = usersByEmailStmt.bind(emailDomain);
		ResultSet restIds = session.execute(idsBS);
		List<Row> idRows = restIds.all();
		for (Row idRow : idRows) {
			Set<String> ids = idRow.getSet("ids", String.class);
			for (String id : ids) {
				BoundStatement bs = getUserStmt.bind(id);
				ResultSet userRst = session.execute(bs);
				List<Row> userRows = userRst.all();
				if (!userRows.isEmpty()) {
					Row row = userRows.get(0);
					User user = new User();
					user.setId(id);
					user.setFirstName(row.getString("firstName"));
					user.setLastName(row.getString("lastName"));
					user.setEmails(row.getSet("emails", String.class));
					user.setPhones(row.getSet("phones", String.class));
					users.add(user);
				}
			}
		}
		return users;
	}

	@Override
	public void addEmail(User user, String email) {
		logger.info("Adding user email " + user + " - " + email);
		// update user
		Set<String> emailWrapper = new HashSet<String>(1);
		emailWrapper.add(email);
		BoundStatement userBS = addUserEmailStmt.bind(emailWrapper, user.getId());
		session.execute(userBS);
		// add email domain
		String emailDomain = Utils.getDomainFromEmail(email);
		Set<String> idWrapper = new HashSet<String>(1);
		idWrapper.add(user.getId());
		BoundStatement emailDomainBS = addEmailDomainStmt.bind(idWrapper, emailDomain, user.getFirstName());
		session.execute(emailDomainBS);
	}

	@Override
	public void addPhoneNumer(User user, String phoneNumber) {
		logger.info("Adding user phone number " + user + " - " + phoneNumber);
		Set<String> phoneWrapper = new HashSet<String>(1);
		phoneWrapper.add(phoneNumber);
		BoundStatement userBS = addUserPhoneNumberStmt.bind(phoneWrapper, user.getId());
		session.execute(userBS);
	}
}
