package cn.tqhweb.jweb.aop.proxy;

import cn.tqhweb.jweb.config.Config;

public class ProxyFactory {
	private static boolean cglib;

	private Config config;

	public ProxyFactory(Config config) {
		this.config = config;
		cglib = config.isUseCglib();
	}

	public ProxyObject get(Class<?> clazz) {
		try {
			ProxyObject proxyInstance;
			if (cglib) {
				proxyInstance = getCglib(clazz);
			} else {
				proxyInstance = getNativeProxy(clazz);
			}
			return proxyInstance;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public ProxyObject getNativeProxy(Class<?> clazz) throws ReflectiveOperationException {
		JdkProxy jdkProxy = new JdkProxy();
		jdkProxy.bind(clazz.getConstructor().newInstance());
		jdkProxy.setInterceptors(config.getInterceptors());
		return jdkProxy;
	}

	private ProxyObject getCglib(Class<?> clazz) throws ReflectiveOperationException {
		CglibProxy cglibProxy = new CglibProxy();
		cglibProxy.bind(clazz.getConstructor().newInstance());
		cglibProxy.setInterceptors(config.getInterceptors());
		return cglibProxy;
	}
}