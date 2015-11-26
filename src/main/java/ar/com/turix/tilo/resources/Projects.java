package ar.com.turix.tilo.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import ar.com.turix.tilo.model.Project;
import ar.com.turix.tilo.utils.Elastic.Index;

@Path("/projects")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Projects extends AbstractResource {

	public Projects() {
		super(Index.projects.name(), Index.projects.type());
	}

	@Path("/")
	@GET
	public List<Project> all() throws Exception {
		List<Project> all = new ArrayList<Project>();
		SearchResponse response = prepareSearch().get();

		for (SearchHit sh : response.getHits().getHits())
			all.add(deserialize(sh.getSourceAsString(), Project.class));

		return all;
	}

	@Path("/")
	@POST
	public void save(Project p) throws Exception {
		if (p.getId() == null) // create
			p.setId(UUID.randomUUID().toString());

		prepareIndex(p.getId()).setSource(serialize(p)).execute().get();
		refresh();
	}

	@Path("/{id}")
	@GET
	public Project get(@PathParam("id") String id) throws Exception {
		return deserialize(prepareGet(id).get().getSourceAsString(), Project.class);
	}

	@Path("/{id}")
	@DELETE
	public void delete(@PathParam("id") String id) throws Exception {
		prepareDelete(id).get();
		refresh();
	}
}