package ar.com.turix.tilo.utils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author rvega
 */
public class MessageInterpolatorMap {
	private static final Locale SYSTEM_LOCALE = new Locale("es", "AR");
	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages");

	private Map<String, Set<String>> messages = new HashMap<>();

	public void put(String path, String template, Object... arguments) {
		Set<String> set = null;
		if ((set = messages.get(path)) == null)
			messages.put(path, set = new HashSet<String>());

		set.add(MessageFormat.format(MESSAGES.getString(template), arguments));
	}

	@Override
	public String toString() {
		return messages.toString();
	}

	public boolean isEmpty() {
		return messages.isEmpty();
	}

	public Map<String, Set<String>> getMessages() {
		return messages;
	}
}