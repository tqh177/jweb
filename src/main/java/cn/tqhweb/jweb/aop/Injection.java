package cn.tqhweb.jweb.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.function.Function;

import javax.servlet.http.HttpSession;

import cn.tqhweb.jweb.JavaBean;
import cn.tqhweb.jweb.aop.annotation.Param;
import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.util.ClassKit;
import cn.tqhweb.jweb.util.ObjKit;
import cn.tqhweb.jweb.util.StrKit;
import cn.tqhweb.jweb.util.log.Logger;

class Injection {
	private static Logger logger = Logger.factory(Injection.class);

	/** 参数注入 */
	static Object[] injectParams(Method method, Request request) throws ReflectiveOperationException {
		Parameter[] parameters = method.getParameters();
		if (parameters.length == 0) {
			return null;
		}
		Object[] params = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			params[i] = getParamValue(request, parameter);
			logger.debug("参数注入: [%s='%s']", parameter.getName(), params[i]);
		}
		return params;
	}

	private static Object getParamValue(Request request, Parameter parameter) throws ReflectiveOperationException {
		Param annotation = parameter.getDeclaredAnnotation(Param.class);
		Class<?> type = parameter.getType();
		Object obj = null;
		if (annotation != null) {
			do {
				if (JavaBean.class.isAssignableFrom(type)) {
					// javabean
					String[] arr = annotation.session();
					if (arr.length > 0) {
						// session注入
						HttpSession session = request.getSession(false);
						if (session != null) {
							if (arr.length == 1 && arr[0].equals(parameter.getName())) {
								Object o = session.getAttribute(arr[0]);
								if (type.isInstance(o)) {
									// 同名JavaBean且类型相同 直接赋值
									obj = o;
									break;
								}
							}
							obj = obj == null ? type.getConstructor().newInstance() : obj;
							obj = injectAttr2Bean(arr, obj, (s) -> session.getAttribute(s));
						}
					}
					arr = annotation.cookie();
					if (arr.length > 0) {
						// cookie注入
						obj = obj == null ? type.getConstructor().newInstance() : obj;
						obj = injectAttr2Bean(arr, type, (s) -> request.getCookie(s));
					}
					arr = annotation.value();
					if (arr.length > 0) {
						// 请求参数注入
						obj = obj == null ? type.getConstructor().newInstance() : obj;
						obj = injectAttr2Bean(arr, type, (s) -> request.get(s));
					}
				} else {
					// 非javaBean类型 直接赋值
					String[] arr = annotation.session();
					if (arr.length > 0) {
						HttpSession session = request.getSession();
						if (session != null) {
							obj = ObjKit.convert(session.getAttribute(arr[0]), type);
						}
						break;
					}
					arr = annotation.cookie();
					if (arr.length > 0) {
						obj = ObjKit.convert(request.getCookie(arr[0]), type);
						break;
					}
					arr = annotation.value();
					if (arr.length > 0) {
						obj = ObjKit.convert(request.get(arr[0]), type);
					}
					break;
				}
			} while (false);
			if (!annotation.allowNull() && obj == null) {
				throw new IllegalArgumentException();
			}
		} else {
			obj = ObjKit.convert(request.get(parameter.getName()), type);
		}
		return obj;
	}

	/** 注入属性到bean */
	private static Object injectAttr2Bean(String[] attr, Object object, Function<String, Object> fun)
			throws ReflectiveOperationException {
		Class<?> clazz = object.getClass();
		logger.debug("注入参数类型: '%s'", clazz.getName());
		// 实例化
		// 向参数对象实例注入属性
		for (int j = 0; j < attr.length; j++) {
			String methodName = "set" + StrKit.ucfirst(attr[j]);
			try {
				Method method = ClassKit.getDeclaredMethodByName(clazz, methodName);
				if (Modifier.isPublic(method.getModifiers())) {
					method.setAccessible(false);
					method.invoke(object, ObjKit.convert(fun.apply(attr[j]), method.getParameterTypes()[0]));
				}
			} catch (NoSuchMethodException | InvocationTargetException e) {
				logger.warn("实例化参数注入时调用%s方法失败,model中不存在相应属性的set方法", methodName);
			}
		}
		return object;
	}
}
