package ar.com.turix.tilo.utils;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

public class Injector {

	@SuppressWarnings("unchecked")
	public static <T> T inject(Class<T> clazz) {
		BeanManager bm = CDI.current().getBeanManager();
		Bean<T> bean = (Bean<T>) bm.getBeans(clazz).iterator().next();
		CreationalContext<T> ctx = bm.createCreationalContext(bean);
		return (T) bm.getReference(bean, clazz, ctx);
	}

	@SuppressWarnings("unchecked")
	public static <T> T inject(String name) {
		BeanManager bm = CDI.current().getBeanManager();
		Bean<T> bean = (Bean<T>) bm.getBeans(name).iterator().next();
		CreationalContext<T> ctx = bm.createCreationalContext(bean);
		return (T) bm.getReference(bean, bean.getBeanClass(), ctx);
	}
}
