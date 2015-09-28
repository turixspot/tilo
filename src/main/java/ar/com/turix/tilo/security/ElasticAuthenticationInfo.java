package ar.com.turix.tilo.security;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

public class ElasticAuthenticationInfo implements AuthenticationInfo {

	private static final long serialVersionUID = 1L;

	private PrincipalCollection principals;
	private String credentials;

	public ElasticAuthenticationInfo(Object principal, String password, String realm) {
		principals = new SimplePrincipalCollection(principal, realm);
		this.credentials = password;
	}

	@Override
	public PrincipalCollection getPrincipals() {
		return principals;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}
}