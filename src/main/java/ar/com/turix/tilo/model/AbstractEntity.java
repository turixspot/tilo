package ar.com.turix.tilo.model;

import ar.com.turix.tilo.utils.EntityValidationException;

/**
 * @author rvega
 */
public abstract class AbstractEntity {

	public abstract String getId();

	public abstract void setId(String string);

	public abstract void validate() throws EntityValidationException;
}
