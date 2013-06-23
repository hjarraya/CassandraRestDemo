package net.hjarraya.cassandrarestdemo.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class User {
	private String id;
	private String firstName;
	private String lastName;
	private Set<String> emails = new HashSet<String>();
	private Set<String> phones = new HashSet<String>();

	public User() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Collection<String> getEmails() {
		return emails;
	}

	public boolean addEmail(String email) {
		return this.emails.add(email);
	}

	public boolean setEmails(Collection<String> emails) {
		return this.emails.addAll(emails);
	}

	public boolean addPhone(String phone) {
		return this.phones.add(phone);
	}

	public Collection<String> getPhones() {
		return phones;
	}

	public boolean setPhones(Collection<String> phones) {
		return this.phones.addAll(phones);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", emails="
				+ Arrays.toString(emails.toArray()) + ", phones=" + Arrays.toString(phones.toArray()) + "]";
	}
}
