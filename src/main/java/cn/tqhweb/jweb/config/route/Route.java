package cn.tqhweb.jweb.config.route;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import cn.tqhweb.jweb.config.IRoute;
import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.util.ClassKit;
import cn.tqhweb.jweb.util.log.Logger;;

public class Route implements IRoute {
	private static Logger logger = Logger.factory(Route.class);
	private List<Rule> methodRule = new ArrayList<>();
	private List<Rule> classRule = new ArrayList<>();

	/**
	 * 自动绑定路由,
	 * 
	 * @param packageName
	 */
	public void autoBind(String packageName) {
		for (Class<?> clazz : ClassKit.getClasses(packageName)) {
			RouteBind annotation = clazz.getDeclaredAnnotation(RouteBind.class);
			if (annotation != null) {
				String curi = annotation.value();
				if (curi.isEmpty()) {
					curi = "/" + clazz.getSimpleName().toLowerCase();
				} else if (curi.charAt(0) != '/') {
					curi = "/" + curi;
				}
				String csuffix = annotation.suffix();
				add(curi, clazz).setSuffix(csuffix);
//				classRule.add(new CRule(curi, clazz).setSuffix(csuffix));
//				logger.debug("添加路由: %s : %s", curi, clazz.getName());
				for (Method method : clazz.getDeclaredMethods()) {
					if (Modifier.isPublic(method.getModifiers())) {
						annotation = method.getDeclaredAnnotation(RouteBind.class);
						if (annotation != null) {
							String value = annotation.value();
							if (value.isEmpty()) {
								continue;
							} else if (value.charAt(0) != '/') {
								value = curi + "/" + value;
							}
							String suffix = annotation.suffix();
							add(value, method).setSuffix(suffix.isEmpty() ? csuffix : suffix);
//							methodRule.add(new MRule(value, method).setSuffix(suffix.isEmpty() ? csuffix : suffix));
//							logger.info("添加路由: %s : %s", value, method.getName());
						}
					}
				}
			}
		}
	}

	@Override
	public Method route(Request request) {
		for (Rule rule : methodRule) {
			Method method = rule.check(request);
			if (method != null) {
				logger.debug("路由成功 => ['%s']", rule.uri);
				return method;
			}
			logger.debug("路由失败 => ['%s']", rule.uri);
		}
		for (Rule rule : classRule) {
			Method method = rule.check(request);
			if (method != null) {
				logger.debug("路由成功 => ['%s']", rule.uri);
				return method;
			}
			logger.debug("路由失败 => ['%s']", rule.uri);
		}
		return null;
	}

	@Override
	public Rule add(String uri, Class<?> clazz) {
		Rule rule = new CRule(uri, clazz);
		classRule.add(rule);
		logger.info("添加路由: %s : %s", uri, clazz.getName());
		return rule;
	}

	@Override
	public Rule add(String uri, Method method) {
		Rule rule = new MRule(uri, method);
		methodRule.add(rule);
		logger.info("添加路由: %s : %s", uri, method.getName());
		return rule;
	}

}
