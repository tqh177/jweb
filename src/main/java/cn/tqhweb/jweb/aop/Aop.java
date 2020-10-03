package cn.tqhweb.jweb.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.tqhweb.jweb.App;
import cn.tqhweb.jweb.aop.annotation.Inject;
import cn.tqhweb.jweb.aop.proxy.ProxyFactory;
import cn.tqhweb.jweb.aop.proxy.ProxyObject;
import cn.tqhweb.jweb.config.Config;
import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.http.Response;
import cn.tqhweb.jweb.util.log.Logger;

public class Aop {
	private static Logger logger = Logger.factory(Aop.class);
	private static Map<Class<?>, ProxyObject> cacheClassMap = new ConcurrentHashMap<>();

	// 获取单例的代理类实例
	public static <T> T get(Class<T> clazz) {
		return get(clazz, true);
	}

	// 获取代理类实例
	public static <T> T get(Class<T> clazz, boolean singleton) {
		ProxyFactory proxyFactory = Config.getConfig().getProxyFactory();
		if (singleton) {
			return getSingleton(clazz, proxyFactory);
		}
		return getNoNSingleton(clazz, proxyFactory);
	}

	public static <T> T inject(T object) {
		return inject(object, null);
	}

	public static <T> T inject(T object, App context) {
		for (Field field : object.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			Inject inject = field.getDeclaredAnnotation(Inject.class);
			if (inject != null) {
				try {
					Class<?> type = field.getType();
					logger.debug("依赖注入 %s", field.getName());
					if (context != null) {
						if (type.isAssignableFrom(Request.class)) {
							field.set(object, context.getRequest());
							continue;
						} else if (type.isAssignableFrom(Response.class)) {
							field.set(object, context.getResponse());
							continue;
						} else if (type.isAssignableFrom(App.class)) {
							field.set(object, context);
							continue;
						}
					}
					if (type.isInstance(object)) {
						field.set(object, getProxy(type, true));
						continue;
					}
					field.set(object, getProxy(type, inject.singleton()));
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return object;
	}

	public static Object[] getMethodArgs(Method method, App context) throws ReflectiveOperationException {
		return Injection.injectParams(method, context.getRequest());
	}

	// 获取单例的代理类实例
	public static <T> T getProxy(Class<T> clazz) {
		return getProxy(clazz, true);
	}

	// 获取代理类实例
	public static <T> T getProxy(Class<T> clazz, boolean singleton) {
		return get(clazz, singleton);
	}

	// 获取单例代理对象
	@SuppressWarnings("unchecked")
	private static <T> T getSingleton(Class<T> clazz, ProxyFactory proxyFactory) {
		ProxyObject object = cacheClassMap.get(clazz);
		if (object == null) {
			object = proxyFactory.get(clazz);
			if (object.getProxyObject().equals(object.getTarget())) {
				cacheClassMap.putIfAbsent(clazz, object);
			}
			inject(object.getTarget());
		}
		return (T) object.getProxyObject();
	}

	// 获取非单例代理对象
	@SuppressWarnings("unchecked")
	private static <T> T getNoNSingleton(Class<?> sourceClass, ProxyFactory proxyFactory) {
		ProxyObject object = Config.getConfig().getProxyFactory().get(sourceClass);
		inject(object.getTarget());
		return (T) object.getProxyObject();
	}
}
