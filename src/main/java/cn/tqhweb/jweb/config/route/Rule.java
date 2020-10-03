package cn.tqhweb.jweb.config.route;

import java.lang.reflect.Method;
import java.util.Map;

import cn.tqhweb.jweb.http.Request;

public abstract class Rule {
	protected String uri;
	protected String suffix = "";
	protected Map<String, String> map;

	Rule setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	abstract Method check(Request request);

}
