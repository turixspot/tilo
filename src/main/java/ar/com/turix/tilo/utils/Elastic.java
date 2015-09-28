package ar.com.turix.tilo.utils;

import javax.inject.Singleton;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

@Singleton
public class Elastic extends TransportClient {

	public Elastic() throws ElasticsearchException {
		super();
		for (String host : settings().getAsArray("client.transport.init")) {
			int port = 9300;

			// or parse it from the host string...
			String[] splitHost = host.split(":", 2);
			if (splitHost.length == 2) {
				host = splitHost[0];
				port = Integer.parseInt(splitHost[1]);
			}

			addTransportAddress(new InetSocketTransportAddress(host, port));
		}
	}
}