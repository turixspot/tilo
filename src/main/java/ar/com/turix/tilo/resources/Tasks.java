package ar.com.turix.tilo.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import ar.com.turix.tilo.model.Task;

@Path("/task")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class Tasks {
	public void save(Task t) {

	}

	public void delete(Long id) {

	}

	public Task get(Long id) {
		return null;
	}

	public List<Task> search(String text) {
		return null;
	}
}