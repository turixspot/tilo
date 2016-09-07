package ar.com.turix.tilo.utils;

import javax.ejb.ApplicationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author rvega
 */
@ApplicationException
public class EntityValidationException extends RuntimeException {
	private Map<String, Set<String>> messages;

	public EntityValidationException(String path, String template) {
		messages = new HashMap<>();
		messages.put(path, new HashSet<>(Arrays.asList(template)));
	}

	public EntityValidationException(Map<String, Set<String>> msgs) {
		this.messages = msgs;
	}

	@Override
	public String getMessage() {
		return messages.toString();
	}

	public Map<String, Set<String>> getMessages() {
		return messages;
	}
}
