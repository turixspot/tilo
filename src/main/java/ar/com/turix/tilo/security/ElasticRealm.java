package ar.com.turix.tilo.security;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import ar.com.turix.tilo.model.User;
import ar.com.turix.tilo.resources.Users;
import ar.com.turix.tilo.utils.Injector;

public class ElasticRealm extends AuthorizingRealm {

	private Users users;

	public ElasticRealm() {
		users = Injector.inject(Users.class);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

		UsernamePasswordToken upt = (UsernamePasswordToken) token;
		String username = upt.getUsername();

		// Null username is invalid
		if (username == null)
			throw new AccountException("Null usernames are not allowed by this realm.");

		try {
			User user = users.get(username);
			return new ElasticAuthenticationInfo(user, user.getPassword(), getName());
		} catch (Exception e) {
			throw new AuthenticationException(e);
		}
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return new ElasticAuthorizationInfo(principals);
	}
}