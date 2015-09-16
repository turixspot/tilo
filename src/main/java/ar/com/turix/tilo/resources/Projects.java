package ar.com.turix.tilo.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import ar.com.turix.tilo.model.Project;

@Path("/projects")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Projects extends AbstractResource {

	@Path("/")
	@GET
	public List<Project> all() throws Exception {
		List<Project> all = new ArrayList<Project>();
		SearchResponse response = client().prepareSearch("projects").execute().get();

		for (SearchHit sh : response.getHits().getHits())
			all.add(deserialize(sh.getSourceAsString(), Project.class));

		return all;
	}

	@Path("/")
	@POST
	public void save(Project p) throws Exception {
		if (p.getId() == null) // create
			p.setId(UUID.randomUUID().toString());

		client().prepareIndex("projects", "project", p.getId()).setSource(serialize(p)).execute().get();
		client().admin().indices().prepareRefresh().execute().get();
	}

	@Path("/{id}")
	@GET
	public Project get(@PathParam("id") String id) throws Exception {
		return deserialize(client().prepareGet("projects", "project", id).execute().get().getSourceAsString(), Project.class);
	}

	@Path("/{id}")
	@PUT
	public void update(@PathParam("id") String id, Project p) throws Exception {
		p.setId(id);
		client().prepareIndex("projects", "project", p.getId()).setSource(serialize(p)).execute().get();
		client().admin().indices().prepareRefresh().execute().get();
	}

	@Path("/{id}")
	@DELETE
	public void delete(@PathParam("id") String id) throws Exception {
		client().prepareDelete("projects", "project", id).execute().get();
		client().admin().indices().prepareRefresh().execute().get();
	}
}