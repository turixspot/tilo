package ar.com.turix.tilo;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import ar.com.turix.tilo.utils.Elastic;

@WebListener
public class IndexesContextListener implements ServletContextListener {

	@Inject
	private Elastic elastic;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			elastic.upgrade();
		} catch (Throwable e) {
			sce.getServletContext().log("An error has ocurred trying to initialize or upgrade indexes", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}