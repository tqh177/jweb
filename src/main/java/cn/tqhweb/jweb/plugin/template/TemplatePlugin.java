package cn.tqhweb.jweb.plugin.template;

import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.http.Response;

public interface TemplatePlugin {
	Response fetch(Request request, Response response, String viewName);
}
