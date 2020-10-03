package cn.tqhweb.jweb.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import cn.tqhweb.jweb.App;
import cn.tqhweb.jweb.exception.HttpException;
import cn.tqhweb.jweb.util.log.Logger;

public class Invocation {
	static private Logger logger = Logger.factory(Invocation.class);
	private Class<?> targetClass;
	private Method method;
	private Object target;
	private Iterator<Interceptor> iterator;
	private App app;
	private Object[] args;

	public Invocation(Class<?> targetClass, Method method) {
		this.targetClass = targetClass;
		this.method = method;
	}

	public void setTarget(Object object) {
		this.target = object;
	}

	public void setArgs(Object[] objects) {
		if (objects == null) {
			return;
		}
		this.args = objects;
	}

	public void setContext(App app) {
		this.app = app;
	}

	public void setInterceptors(Iterator<Interceptor> iterator) {
		this.iterator = iterator;
	}

	/**
	 * 调用方法
	 * 
	 * @throws ReflectiveOperationException
	 */
	public Object invoke() {
		if (iterator != null && iterator.hasNext()) {
			return iterator.next().intercept(this);
		}
		iterator = null;
		Object target = this.target;
		if (target == null) {
			try {
				target = Aop.inject(targetClass.getConstructor().newInstance(), app);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		// 关闭安全检查 提高反射速度
		method.setAccessible(true);
		try {
			return method.invoke(target, getArgs());
		} catch (IllegalArgumentException e) {
			// 方法参数错误
			logger.debug("方法参数错误", e);
			throw new HttpException(404);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof HttpException) {
				throw (HttpException) e.getTargetException();
			} else {
				logger.debug("服务器错误", e);
				throw new HttpException(500, e.getMessage());
			}
		} catch (ReflectiveOperationException e) {
			logger.debug("服务器错误", e);
			throw new HttpException(500, e.getMessage());
		}
	}

	public Object getTarget() {
		return target;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public Method getMethod() {
		return method;
	}

	public App getContext() {
		return app;
	}

	public Object[] getArgs() {
		return args;
	}
	
	public boolean isAction() {
		return app != null;
	}
}