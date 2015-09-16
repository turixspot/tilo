package ar.com.turix.tilo.resources;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.elasticsearch.client.transport.TransportClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractResource {

	private static final ObjectMapper mapper = new ObjectMapper();

	@Context
	private ServletContext ctx;

	protected TransportClient client() {
		return (TransportClient) ctx.getAttribute("es");
	}

	protected byte[] serialize(Object o) throws IOException {
		return mapper.writeValueAsBytes(o);
	}

	protected <T> T deserialize(String src, Class<T> valueType) throws IOException {
		return mapper.readValue(src, valueType);
	}
}