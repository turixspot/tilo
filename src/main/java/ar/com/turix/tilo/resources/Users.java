package ar.com.turix.tilo.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import ar.com.turix.tilo.model.User;
import ar.com.turix.tilo.utils.Elastic.Index;

@Path("/users")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Users extends AbstractResource {

	public Users() {
		super(Index.users.name(), Index.users.type());
	}

	@Path("/")
	@GET
	public List<User> all() throws Exception {
		List<User> all = new ArrayList<User>();
		SearchResponse response = prepareSearch().execute().get();

		for (SearchHit sh : response.getHits().getHits())
			all.add(deserialize(sh.getSourceAsString(), User.class));

		return all;
	}

	@Path("/")
	@POST
	public void save(User p) throws Exception {
		prepareIndex(p.getId()).setSource(serialize(p)).get();
		refresh();
	}

	@Path("/{id}")
	@GET
	public User get(@PathParam("id") String id) throws Exception {
		return deserialize(prepareGet(id).get().getSourceAsString(), User.class);
	}

	@Path("/{id}")
	@DELETE
	public void delete(@PathParam("id") String id) throws Exception {
		prepareDelete(id).get();
		refresh();
	}
}