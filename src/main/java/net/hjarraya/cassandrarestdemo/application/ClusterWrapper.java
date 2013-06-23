package net.hjarraya.cassandrarestdemo.application;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;

public class ClusterWrapper {
	private final Cluster cluster;

	public ClusterWrapper(String contactPoint) {
		cluster = Cluster.builder().addContactPoint(contactPoint).build();
		Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(),
				host.getAddress(), host.getRack());
		}
	}

	public Cluster getCluster() {
		return this.cluster;
	}
}
