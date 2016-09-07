package ar.com.turix.tilo.resources;

import ar.com.turix.tilo.model.Project;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/projects")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Projects extends AbstractResource<Project> {
}