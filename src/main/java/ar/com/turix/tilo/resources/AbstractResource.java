package ar.com.turix.tilo.resources;

import java.io.IOException;

import javax.inject.Inject;

import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;

import ar.com.turix.tilo.utils.Elastic;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractResource {

	private static final ObjectMapper mapper = new ObjectMapper();

	private final String index;
	private final String type;

	@Inject
	private Elastic elastic;

	public AbstractResource(Elastic elastic, String index, String type) {
		this.elastic = elastic;
		this.index = index;
		this.type = type;
	}

	public AbstractResource(String index, String type) {
		this.index = index;
		this.type = type;
	}

	protected byte[] serialize(Object o) throws IOException {
		return mapper.writeValueAsBytes(o);
	}

	protected byte[] write(Object o) {
		try {
			return mapper.writeValueAsBytes(o);
		} catch (IOException ioe) {
			throw new ResourceException("Object can not be serialized.", ioe);
		}
	}

	protected <T> T read(String src, Class<T> valueType) {
		try {
			return mapper.readValue(src, valueType);
		} catch (IOException ioe) {
			throw new ResourceException("Object can not be deserialized.", ioe);
		}
	}

	protected <T> T deserialize(String src, Class<T> valueType) throws IOException {
		return mapper.readValue(src, valueType);
	}

	public SearchRequestBuilder prepareSearch() {
		return elastic.client().prepareSearch(index);
	}

	public IndexRequestBuilder prepareIndex(String id) {
		return elastic.client().prepareIndex(index, type, id);
	}

	public void refresh() {
		elastic.client().admin().indices().prepareRefresh(index).get();
	}

	public GetRequestBuilder prepareGet(String id) {
		return elastic.client().prepareGet(index, type, id);
	}

	public DeleteRequestBuilder prepareDelete(String id) {
		return elastic.client().prepareDelete(index, type, id);
	}
}