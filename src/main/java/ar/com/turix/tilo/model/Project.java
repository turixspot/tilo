package ar.com.turix.tilo.model;

import java.util.List;

public class Project {
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
	public String toString() {
		return "Project [name=" + name + ", attributes=" + attributes + ", tasks=" + tasks + "]";
	}
}