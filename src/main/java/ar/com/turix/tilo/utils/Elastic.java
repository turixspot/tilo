package ar.com.turix.tilo.utils;

import java.io.Closeable;
import java.io.IOException;

import javax.inject.Singleton;

import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.google.common.net.InetAddresses;

@Singleton
public class Elastic implements Closeable {

	private static final String VERSION = "2.0";

	public static enum Indices {
		UPGRADE;

		public String normalized() {
			return "tilo-" + this.name();
		}

		public String versioned() {
			return "tilo-" + VERSION + this.name();
		}
	}

	private TransportClient client;

	public Elastic() {
		client = TransportClient.builder().build();
		connect();
	}

	/**
	 * Look for client.transport.init property at elasticsearch.yml to add configured transport addresses. <br>
	 * <br>
	 * <code>
	 * client.transport.init: ["localhost:9300", "anotherhost:9300"]
	 * </code>
	 */
	private void connect() {
		for (String host : client.settings().getAsArray("client.transport.init")) {
			int port = 9300;

			// or parse it from the host string...
			String[] splitHost = host.split(":", 2);
			if (splitHost.length == 2) {
				host = splitHost[0];
				port = Integer.parseInt(splitHost[1]);
			}

			client.addTransportAddress(new InetSocketTransportAddress(InetAddresses.forString(host), port));
		}
	}

	/**
	 * 
	 */
	public synchronized void upgrade() {
		initialize();
	}

	/**
	 * Verify if a previous installation exist, otherwise create basic data structures.
	 */
	private void initialize() {
		IndicesAdminClient indices = client.admin().indices();
		if (client.prepareExists(Indices.UPGRADE.normalized()).get().exists()) {

			// get latest version
			String latest = null;
			Boolean upgraded = Boolean.FALSE;

			// get current version
			String current = null;

			if (current.equals(latest) || !upgraded) {
				// Create indices

				// Bulk copy.

				// Update Alias

				// Close old indexes.
			}

		} else {
			// Create indices
		}
	}

	@Override
	public void close() throws IOException {
		client.close();
	}
}