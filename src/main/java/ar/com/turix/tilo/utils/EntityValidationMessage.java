package ar.com.turix.tilo.utils;

import java.util.Map;
import java.util.Set;

/**
 * @author rvega
 */
public class EntityValidationMessage {
	private Map<String, Set<String>> details;

	public EntityValidationMessage(Map<String, Set<String>> detail) {
		this.details = detail;
	}

	public Map<String, Set<String>> getDetails() {
		return details;
	}

	public void setDetails(Map<String, Set<String>> details) {
		this.details = details;
	}
}