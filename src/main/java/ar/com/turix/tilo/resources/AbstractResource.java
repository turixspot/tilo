package ar.com.turix.tilo.resources;

import java.io.IOException;

import javax.inject.Inject;

import ar.com.turix.tilo.utils.Elastic;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractResource {

	private static final ObjectMapper mapper = new ObjectMapper();

	@Inject
	protected Elastic client;

	protected byte[] serialize(Object o) throws IOException {
		return mapper.writeValueAsBytes(o);
	}

	protected <T> T deserialize(String src, Class<T> valueType) throws IOException {
		return mapper.readValue(src, valueType);
	}
}