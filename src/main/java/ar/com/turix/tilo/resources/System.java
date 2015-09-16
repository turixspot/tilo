package ar.com.turix.tilo.resources;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequestBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;

@Path("/search")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class System {

	@Context
	private ServletContext ctx;
	
	

	@GET
	@Path("/health")
	public ClusterHealthResponse health() throws Exception {
		ClusterAdminClient c = ((Client) ctx.getAttribute("es")).admin().cluster();
		return c.health(new ClusterHealthRequestBuilder(c).request()).get();
	}
}