package cn.tqhweb.jweb.aop.proxy;

import java.lang.reflect.Method;
import java.util.List;

import cn.tqhweb.jweb.aop.Interceptor;
import cn.tqhweb.jweb.aop.Invocation;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

public class CglibProxy implements InvocationHandler, ProxyObject {
	private Object target;
	private List<Interceptor> interceptors;
	private Object proxyObject;

	public Object bind(Object object) {
		this.target = object;
		Enhancer enhancer = new Enhancer();
		enhancer.setUseCache(true);
		enhancer.setSuperclass(object.getClass());
		enhancer.setCallback(this);
		return proxyObject = enhancer.create();
	}

	@Override
	public void setInterceptors(List<Interceptor> interceptors) {
		this.interceptors = interceptors;
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
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Invocation invocation = new Invocation(target.getClass(), method);
		invocation.setTarget(target);
		invocation.setArgs(args);
		invocation.setInterceptors(interceptors.iterator());
		return invocation.invoke();
	}
}
