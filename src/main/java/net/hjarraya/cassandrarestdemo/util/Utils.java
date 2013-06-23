package net.hjarraya.cassandrarestdemo.util;

public class Utils {
	public static String getDomainFromEmail(String email) {
		return email.substring(email.indexOf('@') + 1, email.length());
	}
}
