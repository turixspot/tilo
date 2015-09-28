package ar.com.turix.tilo.security;

import java.util.Collection;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.PrincipalCollection;

import ar.com.turix.tilo.model.User;

public class ElasticAuthorizationInfo implements AuthorizationInfo {

	private static final long serialVersionUID = 1L;

	private PrincipalCollection principals;

	public ElasticAuthorizationInfo(PrincipalCollection principals) {
		super();
		this.principals = principals;
	}

	@Override
	public Collection<String> getRoles() {
		return ((User) principals.getPrimaryPrincipal()).getRoles();
	}

	@Override
	public Collection<String> getStringPermissions() {
		return null;
	}

	@Override
	public Collection<Permission> getObjectPermissions() {
		return null;
	}
}