package cn.tqhweb.jweb.aop.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import cn.tqhweb.jweb.aop.Interceptor;
import cn.tqhweb.jweb.aop.Invocation;

public class JdkProxy implements ProxyObject, InvocationHandler {

	Object target;
	List<Interceptor> interceptors;
	private Object proxyObject;

	/**
	 * 当改对象没有接口时, 拒绝代理，返回null
	 * @param object
	 * @return
	 */
	public Object bind(Object object) {
		target = object;
		Class<?> clazz = object.getClass();
		Class<?>[] interfaces = clazz.getInterfaces();
		if (interfaces == null || interfaces.length == 0) {
			return proxyObject = object;
		}
		return proxyObject = Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Invocation invocation = new Invocation(target.getClass(), method);
		invocation.setTarget(target);
		invocation.setArgs(args);
		invocation.setInterceptors(interceptors.iterator());
		return invocation.invoke();
	}

	@Override
	public Object getTarget() {
		return target;
	}

	@Override
	public Object getProxyObject() {
		return proxyObject;
	}
	@Override
	public void setInterceptors(List<Interceptor> interceptors) {
		this.interceptors = interceptors;
	}

}
