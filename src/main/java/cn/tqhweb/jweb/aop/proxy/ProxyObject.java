package cn.tqhweb.jweb.aop.proxy;

import java.util.List;

import cn.tqhweb.jweb.aop.Interceptor;

public interface ProxyObject {
	public abstract Object getTarget();
	public abstract Object getProxyObject();
	public abstract void setInterceptors(List<Interceptor> interceptors);
}
