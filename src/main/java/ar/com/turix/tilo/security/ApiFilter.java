package ar.com.turix.tilo.security;

import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This filter do not redirect to login on access denied.
 *
 * @author rvega
 */
public class ApiFilter extends UserFilter {
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		WebUtils.toHttp(response).sendError(403);
		return false;
	}
}