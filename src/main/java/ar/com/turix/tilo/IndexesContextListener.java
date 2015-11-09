package ar.com.turix.tilo;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import ar.com.turix.tilo.utils.Elastic;

@WebListener
public class IndexesContextListener implements ServletContextListener {

	@Inject
	private Elastic elastic;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			IndicesAdminClient client = elastic.admin().indices();
			if (!client.prepareExists("logs").get().isExists()) {
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

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}