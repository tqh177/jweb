package cn.tqhweb.jweb.config;

import java.lang.reflect.Method;

import cn.tqhweb.jweb.config.route.Rule;
import cn.tqhweb.jweb.http.Request;

public interface IRoute {
	Method route(Request request);
	Rule add(String uri, Class<?> clazz);
	Rule add(String uri, Method method);
}
