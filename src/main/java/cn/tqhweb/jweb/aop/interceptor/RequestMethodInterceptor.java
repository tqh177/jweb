package cn.tqhweb.jweb.aop.interceptor;

import java.lang.reflect.AnnotatedElement;

import cn.tqhweb.jweb.aop.Interceptor;
import cn.tqhweb.jweb.aop.Invocation;
import cn.tqhweb.jweb.aop.annotation.AllowMethod;
import cn.tqhweb.jweb.exception.HttpException;

public class RequestMethodInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) {
		if (invocation.isAction()) {
			String method = invocation.getContext().getRequest().getMethod();
			int flag = intercept(invocation.getMethod(), method);
			if (flag == 1 || (flag == 0 && intercept(invocation.getTargetClass(), method) == 1)) {
				throw new HttpException(405, "不被允许的方法: " + method);
			}
		}
		return invocation.invoke();
	}

	private int methodInt(String methodString) {
		int method = 0;
		switch (methodString) {
		case "GET":
			method = AllowMethod.GET;
			break;
		case "POST":
			method = AllowMethod.POST;
			break;
		case "PUT":
			method = AllowMethod.PUT;
			break;
		case "DELETE":
			method = AllowMethod.DELETE;
			break;
		default:
			break;
		}
		return method;
	}

	private int intercept(AnnotatedElement element, String method) {
		AllowMethod allowMethod = element.getAnnotation(AllowMethod.class);
		if (allowMethod == null) {
			return 0;
		}
		if (allowMethod.value() == AllowMethod.NONE) {
			throw new HttpException(404);
		}
		// 判断请求方法是否允许
		if ((allowMethod.value() & methodInt(method)) == 0) {
			return 1;
		}
		return -1;
	}
}
