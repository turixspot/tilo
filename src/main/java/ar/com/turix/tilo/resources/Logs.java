package ar.com.turix.tilo.resources;

import ar.com.turix.tilo.model.Log;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

@Path("/logs")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Logs extends AbstractResource<Log> {

	@Path("/")
	@GET
	public List<Log> query(@QueryParam("date") long date, @QueryParam("user") String user) throws Exception {
		DateTime tmp = new DateTime(date).withTimeAtStartOfDay();
		long from = tmp.getMillis(); // 00h 00m 00s
		long to = tmp.plusDays(1).withTimeAtStartOfDay().minusSeconds(1).getMillis(); // 23h 59m 59s

		List<Log> all = new ArrayList<Log>();
		SearchResponse response = client.prepareSearch("logs")
				// Query
				.setQuery(QueryBuilders.boolQuery() //
						.must(QueryBuilders.rangeQuery("timestamp").from(from).to(to)) //
						.must(QueryBuilders.termQuery("user", user)) //
				).execute().get();

		for (SearchHit sh : response.getHits().getHits())
			all.add(deserialize(sh.getSourceAsString(), Log.class));

		return all;
	}
}