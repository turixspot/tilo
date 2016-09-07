package ar.com.turix.tilo.resources;

import ar.com.turix.tilo.utils.Elastic;
import com.opencsv.CSVWriter;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static org.elasticsearch.search.aggregations.AggregationBuilders.sum;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

@Path("/analytics")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Analytics {

	@Inject
	protected Elastic client;

	@Path("/overview/{from}/{to}")
	@GET
	public Response overview(@PathParam("from") long from, @PathParam("to") long to) throws Exception {

		final SearchResponse response = client.prepareSearch("logs")//
				// Query
				.setQuery(QueryBuilders.rangeQuery("timestamp").from(from).to(to))
				// Aggregations
				.addAggregation(terms("byproject").field("project").subAggregation(sum("time_ms").field("time")))//
				.addAggregation(terms("byuser").field("user").subAggregation(sum("time_ms").field("time")))//
				.addAggregation(terms("detailed").field("project").subAggregation( //
						terms("bytask").field("task").subAggregation( //
								terms("byuser").field("user").subAggregation( //
										sum("time_ms").field("time")//
								))))//
				.execute().get();

		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				XContentBuilder builder = XContentFactory.jsonBuilder(output);
				builder.prettyPrint();
				builder.startObject();
				builder.field("results").value(response.getHits().totalHits() > 0);
				for (Aggregation aggregation : response.getAggregations())
					((InternalAggregation) aggregation).toXContent(builder, ToXContent.EMPTY_PARAMS);
				builder.endObject();
				builder.close();
			}
		};

		return Response.ok(stream).build();
	}

	@Path("/export/csv/{from}/{to}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@GET
	public Response export(@PathParam("from") long from, @PathParam("to") long to) throws Exception {
		final SearchResponse response = client.prepareSearch("logs")//
				// Query
				.setQuery(QueryBuilders.rangeQuery("timestamp").from(from).to(to))
				// Aggregations
				.addAggregation(terms("detailed").field("project").subAggregation( //
						terms("bytask").field("task").subAggregation( //
								terms("byuser").field("user").subAggregation( //
										sum("time_ms").field("time")//
								))))//
				.execute().get();

		StreamingOutput stream = new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(output))) {

					writer.writeNext(new String[] { "Project", "Task", "User", "Time in Hours" });

					Terms project = response.getAggregations().get("detailed");
					for (Terms.Bucket p : project.getBuckets())
						for (Terms.Bucket t : p.getAggregations().<Terms>get("bytask").getBuckets())
							for (Terms.Bucket u : t.getAggregations().<Terms>get("byuser").getBuckets())
								writer.writeNext(new String[] { p.getKey(), t.getKey(), u.getKey(),
										(u.getAggregations().<Sum>get("time_ms").getValue() / (1000 * 60 * 60)) + "" });

					writer.flush();
				}
			}
		};

		return Response.ok(stream).header("content-disposition", "attachment; filename=\"analytics.csv\"").build();
	}
}