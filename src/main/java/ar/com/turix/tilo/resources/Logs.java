package ar.com.turix.tilo.resources;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import ar.com.turix.tilo.model.Log;

@Path("/logs")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Logs extends AbstractResource {

	@Path("/")
	@POST
	public void save(Log l) throws Exception {
		if (l.getId() == null) // create
			l.setId(UUID.randomUUID().toString());

		client().prepareIndex("logs", "log", l.getId()).setSource(serialize(l)).execute().get();
		client().admin().indices().prepareRefresh("logs").execute().get();
	}

	@Path("/{id}")
	@GET
	public Log get(@PathParam("id") String id) throws Exception {
		return deserialize(client().prepareGet("logs", "log", id).execute().get().getSourceAsString(), Log.class);
	}

	@Path("/{id}")
	@DELETE
	public void delete(@PathParam("id") String id) throws Exception {
		client().prepareDelete("logs", "log", id).execute().get();
		client().admin().indices().prepareRefresh("logs").execute().get();
	}

}