package ar.com.turix.tilo.resources;

import static org.elasticsearch.search.aggregations.AggregationBuilders.sum;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;

@Path("/analytics")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Analytics extends AbstractResource {

	@Path("/overview/{from}/{to}")
	@GET
	public Aggs overview(@PathParam("from") long from, @PathParam("to") long to) throws Exception {

		SearchResponse response = client.prepareSearch("logs")//
				// Query
				.setQuery(QueryBuilders.rangeQuery("timestamp").from(from).to(to))
				// Aggregations
				.addAggregation(terms("byproject").field("project").subAggregation(sum("time_ms").field("time")))//
				.addAggregation(terms("byuser").field("user").subAggregation(sum("time_ms").field("time")))//
				.execute().get();

		Aggs aggs = new Aggs();
		for (String name : new String[] { "byproject", "byuser" }) {
			Terms terms = response.getAggregations().get(name);
			Map<String, Number> agg = new HashMap<String, Number>();
			for (Terms.Bucket b : terms.getBuckets()) {
				agg.put(b.getKey(), b.getAggregations().<Sum> get("time_ms").getValue());

			}
			aggs.put(name, agg);
		}

		return aggs;

		// StreamingOutput stream = new StreamingOutput() {
		//
		// @Override
		// public void write(OutputStream output) throws IOException, WebApplicationException {
		// XContentBuilder builder = XContentFactory.jsonBuilder(output);
		// builder.prettyPrint();
		// builder.startObject();
		// response.toXContent(builder, ToXContent.EMPTY_PARAMS);
		// builder.endObject();
		// builder.close();
		// }
		// };

		// return Response.ok(stream).build();
	}

	public static class Aggs {
		private Map<String, Map<String, Number>> aggregations = new HashMap<String, Map<String, Number>>();

		public void put(String name, Map<String, Number> aggregation) {
			aggregations.put(name, aggregation);
		}

		public Map<String, Map<String, Number>> getAggregations() {
			return aggregations;
		}
	}
}