package ar.com.turix.tilo.es;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ElasticListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		TransportClient client = new TransportClient();
		for (String host : client.settings().getAsArray("transportclient.initial_nodes")) {
			int port = 9300;

			// or parse it from the host string...
			String[] splitHost = host.split(":", 2);
			if (splitHost.length == 2) {
				host = splitHost[0];
				port = Integer.parseInt(splitHost[1]);
			}

			client.addTransportAddress(new InetSocketTransportAddress(host, port));
		}

		sce.getServletContext().log(client.settings().toString());
		sce.getServletContext().setAttribute("es", client);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		((TransportClient) sce.getServletContext().getAttribute("es")).close();
	}
}