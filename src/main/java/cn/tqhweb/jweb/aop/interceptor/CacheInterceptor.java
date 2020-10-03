package cn.tqhweb.jweb.aop.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import cn.tqhweb.jweb.App;
import cn.tqhweb.jweb.aop.Interceptor;
import cn.tqhweb.jweb.aop.Invocation;
import cn.tqhweb.jweb.aop.annotation.Cacheable;
import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.http.Response;
import cn.tqhweb.jweb.plugin.cache.CachePlugin;
import cn.tqhweb.jweb.util.log.Level;
import cn.tqhweb.jweb.util.log.Logger;

public class CacheInterceptor implements Interceptor {
	private static Logger logger = Logger.factory(CacheInterceptor.class, Level.DEBUG);

	@Override
	public Object intercept(Invocation invocation) {
		String[] arr = parseNameKey(invocation);
		if (arr == null) {
			return invocation.invoke();
		}
		String name = arr[0];
		String key = arr[1];
		Object object = getCache(name, key);
		if (object == null) {
			object = invocation.invoke();
			setCache(name, key, object);
			if (invocation.isAction()) {
				return html(invocation, object);
			}
		} else if (invocation.isAction()) {
			return html304(invocation, object);
		}
		return object;
	}

	private Object html304(Invocation invocation, Object object) {
		App app = invocation.getContext();
		Request request = app.getRequest();
		Response response = app.getResponse();
		long date = request.getDateHeader("If-Modified-Since");
		// 判断是否可以返回304状态码
		if (date != -1) {
			response.addDateHeader("Expires", date + 120000L);
			response.addDateHeader("Last-modified", date);
			response.setStatus(304);
			return response;
		}
		return object;
	}

	// 让浏览器缓存
	private Object html(Invocation invocation, Object object) {
		App app = invocation.getContext();
		Response response = app.getResponse();
		long now = System.currentTimeMillis();
		response.addDateHeader("Expires", now + 120000L);
		response.addDateHeader("Last-modified", now);
		return object;
	}

	private String[] parseNameKey(Invocation invocation) {
		Class<?> targetClass = invocation.getTargetClass();
		Cacheable cacheable = targetClass.getAnnotation(Cacheable.class);
		String cname = null;
		if (cacheable != null) {
			cname = cacheable.value();
		}
		Method method = invocation.getMethod();
		cacheable = method.getDeclaredAnnotation(Cacheable.class);
		if (cacheable != null) {
			String name = cacheable.key();
			if (name.isEmpty()) {
				name = cname;
			}
			String key = cacheable.value();
			key = parseValue(key, method.getParameters(), invocation.getArgs());
			if (key == null) {
				return null;
			}
			return new String[] { name, key };
		}
		return null;
	}

	private Object getCache(String name, String key) {
		CachePlugin plugin = CachePlugin.getCachePlugin();
		if (plugin == null) {
			return null;
		}
		Object object = plugin.get(name, key);
		if (object != null) {
			logger.debug("using cached [%s@%s]", name, key);
		}
		return object;
	}

//	private long getCacheTime(String name, String key) {
//		CachePlugin plugin = CachePlugin.getCachePlugin();
//		if (plugin == null) {
//			return plugin.getTime(name, key);
//		}
//		return -1;
//	}

	private void setCache(String name, String key, Object value) {
		CachePlugin plugin = CachePlugin.getCachePlugin();
		if (value != null && plugin != null) {
			logger.debug("setting cached [%s@%s]", name, key);
			if (value instanceof Response) {
				plugin.set(name, key, ((Response) value).getContent());
			} else {
				plugin.set(name, key, value);
			}
		}
	}

	// 解析${参数名}值
	private String parseValue(String value, Parameter[] parameters, Object[] args) {
		int i = value.indexOf('$');
		if (i == -1) {
			return value;
		}
		int from = 0, end = i, length = value.length();
		StringBuilder builder = new StringBuilder(length);
		builder.append(value, from, end - from);
		for (; end < length; end++) {
			if (value.charAt(end) == '$' && value.charAt(end + 1) == '{') {
				from = end + 2;
				end = value.indexOf('}', from);
				String key = value.substring(from, end);
				for (int k = 0; k < parameters.length; k++) {
					if (parameters[k].getName().equals(key)) {
						key = String.valueOf(args[k]);
						break;
					}
				}
				builder.append(key);
			} else {
				builder.append(value.charAt(i));
			}
		}
		return builder.toString();
	}
}
