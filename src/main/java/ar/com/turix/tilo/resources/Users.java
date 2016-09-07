package ar.com.turix.tilo.resources;

import ar.com.turix.tilo.model.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/users")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Users extends AbstractResource<User> {
}