package ar.com.turix.tilo.resources;

import ar.com.turix.tilo.model.AbstractEntity;
import ar.com.turix.tilo.utils.Elastic;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author rvega
 */
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AbstractResource<T extends AbstractEntity> {

	private static final ObjectMapper mapper = new ObjectMapper();
	private static final TimeValue time = new TimeValue(60000);

	@Inject
	protected Elastic client;

	protected Class<T> genericType;
	private String type;
	private String index;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AbstractResource() {
		Class<?> cls = getClass();
		while (!(cls.getSuperclass() == null || cls.getSuperclass().equals(AbstractResource.class))) {
			cls = cls.getSuperclass();
		}

		if (cls.getSuperclass() == null)
			throw new RuntimeException("Unexpected exception occurred.");

		this.genericType = ((Class) ((ParameterizedType) cls.getGenericSuperclass()).getActualTypeArguments()[0]);

		this.type = this.genericType.getSimpleName().toLowerCase();
		this.index = this.type + "s";
	}

	protected byte[] serialize(Object o) throws IOException {
		return mapper.writeValueAsBytes(o);
	}

	protected <T> T deserialize(String src, Class<T> valueType) throws IOException {
		return mapper.readValue(src, valueType);
	}

	@GET
	@Path("/")
	public List<T> all() throws IOException {
		List<T> all = new ArrayList<>();

		SearchResponse response = client.prepareSearch(index).setScroll(time).setSize(100).execute().actionGet();
		do {
			for (SearchHit hit : response.getHits().getHits())
				all.add(deserialize(hit.getSourceAsString(), genericType));

			response = client.prepareSearchScroll(response.getScrollId()).setScroll(time).execute().actionGet();

		} while (response.getHits().getHits().length != 0);

		return all;
	}

	@POST
	@Path("/")
	public void save(T p) throws ExecutionException, InterruptedException, IOException {
		if (p.getId() == null) // create
			p.setId(UUID.randomUUID().toString());

		p.validate();

		client.prepareIndex(index, type, p.getId()).setSource(serialize(p)).execute().get();
		client.admin().indices().prepareRefresh(index).execute().get();
	}

	@GET
	@Path("/{id}")
	public T get(@PathParam("id") String id) throws ExecutionException, InterruptedException, IOException {
		return deserialize(client.prepareGet(index, type, id).execute().get().getSourceAsString(), genericType);
	}

	@DELETE
	@Path("/{id}")
	public void delete(@PathParam("id") String id) throws ExecutionException, InterruptedException, IOException {
		client.prepareDelete(index, type, id).execute().get();
		client.admin().indices().prepareRefresh().execute().get();
	}
}