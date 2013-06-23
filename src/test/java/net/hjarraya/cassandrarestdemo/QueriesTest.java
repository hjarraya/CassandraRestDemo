package net.hjarraya.cassandrarestdemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.hjarraya.cassandrarestdemo.model.User;

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
	private static final String INSERT_STATEMENT = "INSERT INTO users.users (id, firstName, lastName, emails, phoneNumbers) VALUES (?, ?, ?, ?,?);";
	private static final String SELECT_STATEMENT = "SELECT * FROM users.users where id=?;";
	private static final String CREATE_CF = "CREATE TABLE users.users (  id text,  firstName text,  lastName text,  emails set<text>,  phoneNumbers set<text>, PRIMARY KEY (id));";
	private static final String ADD_EMAIL_STATEMENT = "UPDATE users.users SET emails = emails + ? WHERE id = ?;";
	private static Cluster cluster;
	private static boolean keyspaceCreated;
	private static boolean cfCreated;

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
	}

	public static void createKeySpace() {
		logger.debug("Creating keyspace " + CREATE_KEYSPACE);
		Session session = cluster.connect();
		ResultSet rest = session
				.execute("select * from system.schema_keyspaces where keyspace_name='users';");
		if (rest.all().isEmpty()) {
			session.execute(CREATE_KEYSPACE);
		}
		keyspaceCreated = true;
	}

	public static void createCF() {
		assertTrue(keyspaceCreated);
		logger.debug("Creating column family " + CREATE_CF);
		Session session = cluster.connect();
		ResultSet rest = session.execute("select * from system.schema_columns where keyspace_name='users';");
		if (rest.all().isEmpty()) {
			session.execute(CREATE_CF);
		}
		cfCreated = true;
	}

	@Test
	public void addUserTest() {
		assertTrue(keyspaceCreated);
		assertTrue(cfCreated);
		logger.debug("Creating user " + INSERT_STATEMENT);
		User user = createUser();
		Session session = cluster.connect();
		PreparedStatement insertSt = session.prepare(INSERT_STATEMENT);
		insertSt.setConsistencyLevel(ConsistencyLevel.ONE);
		BoundStatement bs = insertSt.bind();
		bs.bind(user.getId(), user.getFirstName(), user.getLastName(), user.getEmails(), user.getPhones());
		session.execute(bs);
		// check user was added
		User actualUser = getUser(user.getId());
		assertTrue(user.getId().equals(actualUser.getId()));
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
		logger.debug("Retreiving user id " + id);
		User user = new User();
		Session session = cluster.connect();
		PreparedStatement selectSt = session.prepare(SELECT_STATEMENT);
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
	public void addEmailTest() {
		logger.debug("Adding user email " + INSERT_STATEMENT);
		addUserTest();
		User user = createUser();
		String userId = "1";
		String email = "john.doe2@gmail.com";
		Set<String> emails = new HashSet<String>();
		emails.add(email);
		user.addEmail(email);
		Session session = cluster.connect();
		PreparedStatement ps = session.prepare(ADD_EMAIL_STATEMENT);
		session.execute(ps.bind(emails, userId));
		User actualUser = getUser(userId);
		assertEquals(actualUser.getEmails(), user.getEmails());
	}
}
