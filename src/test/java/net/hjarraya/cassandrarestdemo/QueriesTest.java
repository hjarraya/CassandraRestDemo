package net.hjarraya.cassandrarestdemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.hjarraya.cassandrarestdemo.model.User;
import net.hjarraya.cassandrarestdemo.util.Utils;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class QueriesTest {
	private static final Logger logger = Logger.getLogger(QueriesTest.class);
	private static final String CREATE_KEYSPACE = "CREATE KEYSPACE users  WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor': 1};";
	private static final String CREATE_USERS_CF = "CREATE TABLE users.users (  id text,  firstName text,  lastName text,  emails set<text>,  phoneNumbers set<text>, PRIMARY KEY (id));";
	private static final String CREATE_EMAILS_CF = "CREATE TABLE users.emaildomain ( domain text, firstName text, ids set<text>, PRIMARY KEY (domain,firstName) );";
	private static final String CREATE_FN_INDEX = "CREATE INDEX usersFirstName ON users.users(firstName);";
	private static final String CREATE_LN_INDEX = " CREATE INDEX usersLastName ON users.users(lastName);";
	private static final String INSERT_USER = "UPDATE users.users SET firstName=? , lastName = ? , emails = ?, phoneNumbers = ? WHERE id =?;";
	private static final String SELECT_USER = "SELECT * FROM users.users WHERE id=?;";
	private static final String DELET_USER = "DELETE FROM users.users WHERE id=?;";
	private static final String SELECT_USER_BY_FN = "SELECT * FROM users.users WHERE firstName=?;";
	private static final String SELECT_USER_BY_LN = "SELECT * FROM users.users WHERE lastName=?;";
	private static final String SELECT_USER_BY_FN_LN = "SELECT * FROM users WHERE firstName=? AND lastName=?;";
	private static final String SELECT_USERS_BY_EMAIL = "SELECT * FROM users.emaildomain WHERE domain=?;";
	private static final String ADD_EMAIL_DOMAIN = "UPDATE users.emaildomain SET  ids = ids + ? where domain =? and firstName = ?";
	private static final String ADD_EMAIl = "UPDATE users.users SET emails = emails + ? WHERE id = ?;";
	private static final String ADD_PHONE = "UPDATE users.users SET phoneNumbers = phoneNumbers + ? WHERE id = ?;";
	private static Cluster cluster;

	@BeforeClass
	public static void init() {
		BasicConfigurator.configure();
		logger.debug("ini");
		cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(),
				host.getAddress(), host.getRack());
		}
		createKeySpace();
		createCF();
		createIndex();
	}

	public static void createKeySpace() {
		logger.debug("Creating keyspace " + CREATE_KEYSPACE);
		Session session = cluster.connect();
		ResultSet rest = session
				.execute("select * from system.schema_keyspaces where keyspace_name='users';");
		if (rest.all().isEmpty()) {
			session.execute(CREATE_KEYSPACE);
		}
	}

	public static void createCF() {
		logger.debug("Creating column family " + CREATE_USERS_CF);
		Session session = cluster.connect();
		ResultSet rest = session.execute("select * from system.schema_columns where keyspace_name='users';");
		if (rest.all().isEmpty()) {
			session.execute(CREATE_USERS_CF);
		}
		logger.debug("Creating column family " + CREATE_EMAILS_CF);
		rest = session.execute("select * from system.schema_columns where keyspace_name='users';");
		if (rest.all().isEmpty()) {
			session.execute(CREATE_EMAILS_CF);
		}
	}

	public static void createIndex() {
		logger.debug("Creating secondary index" + CREATE_FN_INDEX);
		Session session = cluster.connect();
		ResultSet rest = session
				.execute("select index_name from system.schema_columns where keyspace_name='users' AND columnfamily_name='users' and column_name='firstname';");
		if (rest.all().isEmpty()) {
			session.execute(CREATE_FN_INDEX);
		}
		logger.debug("Creating column family " + CREATE_LN_INDEX);
		rest = session
				.execute("select index_name from system.schema_columns where keyspace_name='users' AND columnfamily_name='users' and column_name='lastname';");
		if (rest.all().isEmpty()) {
			session.execute(CREATE_LN_INDEX);
		}
	}

	@Test
	public void addUserTest() {
		logger.debug("Creating user " + INSERT_USER);
		User user = createUser();
		Session session = cluster.connect();
		PreparedStatement insertSt = session.prepare(INSERT_USER);
		insertSt.setConsistencyLevel(ConsistencyLevel.ONE);
		BoundStatement bs = insertSt.bind();
		bs.bind(user.getFirstName(), user.getLastName(), user.getEmails(), user.getPhones(), user.getId());
		session.execute(bs);
		// check user was added
		User actualUser = getUser(user.getId());
		assertTrue(user.getId().equals(actualUser.getId()));
	}

	@Test
	// TODO move this to utils
	public void retrieveEmailDomainTest() {
		String email = "jhon.doe@gmail.com";
		assertEquals("gmail.com", Utils.getDomainFromEmail(email));
	}

	@Test
	public void addEmailDomain() {
		logger.debug("Creating email domain " + ADD_EMAIL_DOMAIN);
		User user = createUser();
		Session session = cluster.connect();
		PreparedStatement insertSt = session.prepare(ADD_EMAIL_DOMAIN);
		insertSt.setConsistencyLevel(ConsistencyLevel.ONE);
		BoundStatement bs = insertSt.bind();
		String emailDomain = Utils.getDomainFromEmail(user.getEmails().iterator().next());
		Set<String> ids = new HashSet<String>();
		ids.add(user.getId());
		bs.bind(ids, emailDomain, user.getFirstName());
		session.execute(bs);
		// check email domain was added
		PreparedStatement selectSt = session.prepare(SELECT_USERS_BY_EMAIL);
		ResultSet rst = session.execute(selectSt.bind(emailDomain));
		List<Row> rows = rst.all();
		assertTrue(rows.size() == 1);
		for (Row row : rows) {
			Set<String> retrievedIds = row.getSet("ids", String.class);
			for (String id : retrievedIds)
				if (id.equals(user.getId()))
					return;
		}
		fail("Node ids added to email domain");
	}

	public User createUser() {
		User user = new User();
		user.setId("1");
		user.setFirstName("john");
		user.setLastName("Doe");
		user.addEmail("john.doe@gmail.com");
		user.addPhone("777-777-777");
		return user;
	}

	public User getUser(String id) {
		logger.debug("Retreiving user id " + id + " Executing query " + SELECT_USER);
		User user = new User();
		Session session = cluster.connect();
		PreparedStatement selectSt = session.prepare(SELECT_USER);
		ResultSet res = session.execute(selectSt.bind(id));
		List<Row> rows = res.all();
		for (Row row : rows) {
			user.setId(row.getString("id"));
			user.setFirstName(row.getString("firstName"));
			user.setLastName(row.getString("lastName"));
			user.setEmails(row.getSet("emails", String.class));
			user.setPhones(row.getSet("phoneNumbers", String.class));
		}
		return user;
	}

	@Test
	public void deleteUserTest() {
		logger.debug("Deleting user " + DELET_USER);
		addUserTest();
		User user = createUser();
		Session session = cluster.connect();
		PreparedStatement ps = session.prepare(DELET_USER);
		session.execute(ps.bind(user.getId()));
		User actual = getUser(user.getId());
		assertTrue(actual.getId() == null);
	}

	@Test
	public void getUserByFirstName() {
		logger.debug("Getting user by first name " + SELECT_USER_BY_FN);
		addUserTest();
		User user = createUser();
		Session session = cluster.connect();
		PreparedStatement ps = session.prepare(SELECT_USER_BY_FN);
		ResultSet rest = session.execute(ps.bind(user.getFirstName()));
		List<Row> rows = rest.all();
		assertTrue(rows.size() == 1);
		assertTrue(rows.get(0).getString("firstName").equals(user.getFirstName()));
	}

	public void getUserByLastName() {
		logger.debug("Getting user by lastName name " + SELECT_USER_BY_LN);
		addUserTest();
		User user = createUser();
		Session session = cluster.connect();
		PreparedStatement ps = session.prepare(SELECT_USER_BY_LN);
		ResultSet rest = session.execute(ps.bind(user.getLastName()));
		List<Row> rows = rest.all();
		assertTrue(rows.size() == 1);
		assertTrue(rows.get(0).getString("lastName").equals(user.getLastName()));
	}

	public void getUserByFirstAndLastName() {
		logger.debug("Getting user by first and  lastName name " + SELECT_USER_BY_FN_LN);
		addUserTest();
		User user = createUser();
		Session session = cluster.connect();
		PreparedStatement ps = session.prepare(SELECT_USER_BY_FN_LN);
		ResultSet rest = session.execute(ps.bind(user.getFirstName(), user.getLastName()));
		List<Row> rows = rest.all();
		assertTrue(rows.size() == 1);
		assertTrue(rows.get(0).getString("firstName").equals(user.getFirstName()));
		assertTrue(rows.get(0).getString("lastName").equals(user.getLastName()));
	}

	@Test
	public void addEmailTest() {
		logger.debug("Adding user email " + ADD_EMAIl);
		addUserTest();
		User user = createUser();
		String email = "john.doe2@gmail.com";
		user.addEmail(email);
		Session session = cluster.connect();
		PreparedStatement ps = session.prepare(ADD_EMAIl);
		session.execute(ps.bind(user.getEmails(), user.getId()));
		User actualUser = getUser(user.getId());
		assertEquals(actualUser.getEmails(), user.getEmails());
	}

	@Test
	// ADD_PHONE
	public void addPhoneTest() {
		logger.debug("Adding user phone " + ADD_PHONE);
		addUserTest();
		User user = createUser();
		String phone = "555-555-55";
		user.addPhone(phone);
		Session session = cluster.connect();
		PreparedStatement ps = session.prepare(ADD_PHONE);
		session.execute(ps.bind(user.getPhones(), user.getId()));
		User actualUser = getUser(user.getId());
		assertEquals(actualUser.getPhones(), user.getPhones());
	}
}
