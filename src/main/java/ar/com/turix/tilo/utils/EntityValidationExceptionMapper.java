package ar.com.turix.tilo.utils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Implementation of {@link ExceptionMapper} to send own a "400 Bad Request" if
 * business rules validation fails.
 */
@Provider
public class EntityValidationExceptionMapper implements ExceptionMapper<EntityValidationException> {
	@Override
	public Response toResponse(EntityValidationException exception) {
		return Response //
				.status(Response.Status.BAD_REQUEST) //
				.entity(new EntityValidationMessage(exception.getMessages())) //
				.type(MediaType.APPLICATION_JSON) //
				.build();
	}
}