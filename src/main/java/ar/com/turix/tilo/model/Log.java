package ar.com.turix.tilo.model;

import ar.com.turix.tilo.utils.Assert;
import ar.com.turix.tilo.utils.EntityValidationException;

public class Log extends AbstractEntity {
	private String id;
	private String user;
	private long timestamp;
	private String project;
	private String task;
	private long time;
	private String note;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public void validate() throws EntityValidationException {
		new Assert().assertRange(time, 1l, Long.MAX_VALUE, "time", "validation.Log.time.range")//
				.assertNotBlank(project, "project", "validation.Log.project.notBlank")//
				.assertNotBlank(task, "task", "validation.Log.task.notBlank")//
				.verify();
	}
}