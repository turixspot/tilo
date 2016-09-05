package ar.com.turix.tilo.resources;

import ar.com.turix.tilo.model.Project;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/projects")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Projects extends AbstractResource {

	@Path("/")
	@GET
	public List<Project> all() throws Exception {
		List<Project> all = new ArrayList<Project>();

		SearchRequestBuilder rb = client.prepareSearch("projects").addSort("name", SortOrder.ASC);

		int from = 0;
		long total = 0;
		do {
			rb.setFrom(from);
			SearchResponse response = rb.execute().get();

			total = response.getHits().getTotalHits();

			for (SearchHit sh : response.getHits().getHits())
				all.add(deserialize(sh.getSourceAsString(), Project.class));

			from += response.getHits().getHits().length;

		} while (from < total);

		return all;
	}

	@Path("/")
	@POST
	public void save(Project p) throws Exception {
		if (p.getId() == null) // create
			p.setId(UUID.randomUUID().toString());

		client.prepareIndex("projects", "project", p.getId()).setSource(serialize(p)).execute().get();
		client.admin().indices().prepareRefresh("projects").execute().get();
	}

	@Path("/{id}")
	@GET
	public Project get(@PathParam("id") String id) throws Exception {
		return deserialize(client.prepareGet("projects", "project", id).execute().get().getSourceAsString(), Project.class);
	}

	@Path("/{id}")
	@DELETE
	public void delete(@PathParam("id") String id) throws Exception {
		client.prepareDelete("projects", "project", id).execute().get();
		client.admin().indices().prepareRefresh().execute().get();
	}
}