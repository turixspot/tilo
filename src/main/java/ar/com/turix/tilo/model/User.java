package ar.com.turix.tilo.model;

import ar.com.turix.tilo.utils.Assert;
import ar.com.turix.tilo.utils.EntityValidationException;

import java.util.Set;

public class User extends AbstractEntity {
	private String id;
	private String name;
	private Set<String> roles;
	private String password;

	public User() {

	}

	public User(String id, String name, Set<String> roles, String password) {
		this.id = id;
		this.name = name;
		this.roles = roles;
		this.password = password;
	}

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

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void validate() throws EntityValidationException {
		new Assert().assertNotBlank(name, "name", "validation.User.name.notEmpty")//
				.assertNotEmpty(password, "password", "validation.User.password.notEmpty")//
				.verify();
	}

	@Override
	public String toString() {
		return "User{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", roles=" + roles + ", password='" + password + '\'' + '}';
	}
}