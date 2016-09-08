package ar.com.turix.tilo.model;

import ar.com.turix.tilo.utils.Assert;
import ar.com.turix.tilo.utils.EntityValidationException;

import java.util.List;

public class Project extends AbstractEntity {
	private String id;
	private String name;
	private List<Attribute> attributes;
	private List<Task> tasks;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public void validate() throws EntityValidationException {
		Assert a = new Assert()//
				.assertNotBlank(name, "name", "validation.Project.name.notEmpty")//
				.assertNotEmpty(tasks, "tasks", "validation.Project.tasks.notEmpty");

		for (Attribute attribute : attributes)
			a.assertNotBlank(attribute.getName(), "attributes", "validation.Project.attributes.name.notBlank");

		for (Task task : tasks)
			a.assertNotBlank(task.getName(), "tasks", "validation.Project.tasks.name.notBlank");

		a.verify();
	}

	@Override
	public String toString() {
		return "Project [name=" + name + ", attributes=" + attributes + ", tasks=" + tasks + "]";
	}
}