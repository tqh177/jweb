package cn.tqhweb.jweb.config.route;

import java.lang.reflect.Method;

import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.util.ClassKit;
import cn.tqhweb.jweb.util.StrKit;

// 控制器路由规则
public class CRule extends Rule {
	protected Class<?> clazz;

	public CRule(String uri, Class<?> clazz) {
		this.uri = uri;
		this.clazz = clazz;
	}

	@Override
	Method check(Request request) {
		String ruri = request.getUriNoSuffix();
		if (ruri.startsWith(uri) && request.getSuffix().equals(suffix)) {
			String methodName;
			if (ruri.length() == uri.length()) {
				methodName = "index";
			} else {
				methodName = StrKit.ltrim(ruri.substring(uri.length()), '/');
			}
			if (methodName.indexOf('/') != -1) {
				return null;
			}
			try {
				Method method = ClassKit.getDeclaredMethodByName(clazz, methodName);
				return method;
			} catch (NoSuchMethodException e) {
				return null;
			}
		}
		return null;
	}

}
