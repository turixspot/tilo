package ar.com.turix.tilo;

import ar.com.turix.tilo.utils.Elastic;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.IOException;

/**
 * @author rvega
 */
@Startup
@Singleton
public class Bootstrap {

	@Inject
	private Elastic elastic;

	@PostConstruct
	public void initialize() {
		try {
			IndicesAdminClient client = elastic.admin().indices();
			if (!client.prepareExists("log_idx").get().isExists()) {
				CreateIndexRequestBuilder logs = client.prepareCreate("logs");

				final XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()//
						.startObject()//
						.startObject("log") //
						.startObject("properties") //
						.startObject("user").field("type", "string").field("index", "not_analyzed").endObject()//
						.startObject("project").field("type", "string").field("index", "not_analyzed").endObject()//
						.startObject("task").field("type", "string").field("index", "not_analyzed").endObject()//
						.endObject()//
						.endObject()//
						.endObject();
				System.out.println(mappingBuilder.string());
				logs.addMapping("log", mappingBuilder);

				logs.execute().actionGet();
			}
		} catch (ElasticsearchException | IOException e) {
			e.printStackTrace();
		}
	}
}
