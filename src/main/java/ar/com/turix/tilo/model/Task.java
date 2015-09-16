package ar.com.turix.tilo.model;

public class Task {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Task [name=" + name + "]";
	}
}