package ar.com.turix.tilo.utils;

import java.util.Collection;

/**
 * @author rvega
 */
public class Assert {

	private MessageInterpolatorMap msgs = new MessageInterpolatorMap();

	public Assert assertNotNull(Object value, String path, String template) {
		if (value == null)
			msgs.put(path, template);
		return this;
	}

	public Assert assertNotEmpty(String value, String path, String template) {
		if (value == null || value.isEmpty())
			msgs.put(path, template);
		return this;
	}

	public <T> Assert assertNotEmpty(Collection<T> value, String path, String template) {
		if (value == null || value.isEmpty())
			msgs.put(path, template);
		return this;
	}

	public Assert assertNotBlank(String value, String path, String template) {
		if (value == null || value.trim().isEmpty())
			msgs.put(path, template);
		return this;
	}

	public Assert assertSize(String value, Integer min, Integer max, String path, String template) {
		if (value == null || value.length() < min || value.length() > max)
			msgs.put(path, template, min, max);
		return this;
	}

	public <T> Assert assertSize(Collection<T> value, Integer min, Integer max, String path, String template) {
		if (value == null || value.size() < min || value.size() > max)
			msgs.put(path, template, min, max);
		return this;
	}

	public Assert assertEquals(long actual, int expected, String path, String template) {
		if (actual != expected)
			msgs.put(path, template, actual, expected);
		return this;
	}

	public Assert assertRange(long actual, long minValue, long maxValue, String path, String template) {
		if (maxValue < actual || minValue > actual)
			msgs.put(path, template, actual, minValue, maxValue);
		return this;
	}

	public void verify() throws EntityValidationException {
		if (!msgs.isEmpty())
			throw new EntityValidationException(msgs.getMessages());
	}

}