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
import javax.ws.rs.QueryParam;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;

import ar.com.turix.tilo.model.Log;
import ar.com.turix.tilo.utils.Elastic.Index;

@Path("/logs")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Logs extends AbstractResource {

	public Logs() {
		super(Index.logs.name(), Index.logs.type());
	}

	@Path("/")
	@GET
	public List<Log> query(@QueryParam("date") long date, @QueryParam("user") String user) throws Exception {
		DateTime tmp = new DateTime(date).withTimeAtStartOfDay();
		long from = tmp.getMillis(); // 00h 00m 00s
		long to = tmp.plusDays(1).withTimeAtStartOfDay().minusSeconds(1).getMillis(); // 23h 59m 59s

		List<Log> all = new ArrayList<Log>();
		SearchResponse response = prepareSearch()
		// Query
				.setQuery(QueryBuilders.boolQuery() //
						.must(QueryBuilders.rangeQuery("timestamp").from(from).to(to)) //
						.must(QueryBuilders.termQuery("user", user)) //
				).get();

		for (SearchHit sh : response.getHits().getHits())
			all.add(deserialize(sh.getSourceAsString(), Log.class));

		return all;
	}

	@Path("/")
	@POST
	public void save(Log l) throws Exception {
		if (l.getId() == null) // create
			l.setId(UUID.randomUUID().toString());

		prepareIndex(l.getId()).setSource(serialize(l)).get();
		refresh();
	}

	@Path("/{id}")
	@GET
	public Log get(@PathParam("id") String id) throws Exception {
		return deserialize(prepareGet(id).get().getSourceAsString(), Log.class);
	}

	@Path("/{id}")
	@DELETE
	public void delete(@PathParam("id") String id) throws Exception {
		prepareDelete(id).execute().get();
		refresh();
	}
}