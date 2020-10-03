package cn.tqhweb.jweb.config.route;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.util.ObjKit;

// 方法路由规则
public class MRule extends Rule {
	protected Method method;
	protected Parameter[] parameters;
	protected boolean isRegex = false;
	// 参数类型
	protected Map<String, Class<?>> typeMap;

	public MRule(String uri, Method method) {
		if (uri.contains("<") || uri.contains("*")) {
			isRegex = true;
		}
		this.uri = uri;
		this.method = method;
		parameters = method.getParameters();
		typeMap = new HashMap<>();
		for (Parameter parameter : parameters) {
			typeMap.put(parameter.getName(), parameter.getType());
		}
	}

	@Override
	Method check(Request request) {
		if (isRegex) {
			if (!checkPattern(request, uri)) {
				return null;
			}
		} else if (!request.getUriNoSuffix().equals(uri)) {
			return null;
		}
		if (suffix.equals(request.getSuffix()) || "*".equals(suffix)) {
			return method;
		}
		return null;
	}

	protected boolean checkPattern(Request request, String uri) {
		String requestUri = request.getUriNoSuffix();
		Map<String, String> map = new HashMap<>();
		int i = 0, j = 0;
		for (; i < uri.length() && j < requestUri.length(); i++, j++) {
			char c1 = uri.charAt(i);
			char c2 = requestUri.charAt(j);
			if (c1 == '<') {
				i++;
				int end = uri.indexOf('>', i);
				String key = uri.substring(i, end);
				i = end + 1;
				if (i == uri.length()) {
					// 末尾匹配符
					String value = requestUri.substring(j);
					if (value.indexOf('/') >= 0) {
						return false;
					}
					j = requestUri.length();
					if (ObjKit.convert(value, typeMap.get(key)) == null) {
						// 方法类型与路由匹配字符串类型不一致 该条路由规则匹配失败
						return false;
					}
					map.put(key, value);
					break;
				} else {
					char c3 = uri.charAt(i);
					int start = j;
					j = requestUri.indexOf(c3, start);
					if (j == -1) {
						return false;
					}
					String value = requestUri.substring(start, j);
					if (ObjKit.convert(value, typeMap.get(key)) == null) {
						// 方法类型与路由匹配字符串类型不一致 该条路由规则匹配失败
						return false;
					}
					map.put(key, value);
				}
			} else if (c1 == '*') {
				i++;
				if (i == uri.length()) {
					return true;
				}else {
					j = requestUri.indexOf(uri.charAt(i), j);
					if (j == -1) {
						return false;
					}
				}
			} else if (c1 != c2) {
				return false;
			}
		}
		if (i == uri.length() && j == requestUri.length()) {
			request.addParam(map);
			return true;
		}
		return false;
	}
}
