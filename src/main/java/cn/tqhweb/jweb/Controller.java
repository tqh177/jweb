package cn.tqhweb.jweb;

import java.util.Map;
import java.util.Map.Entry;

import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.http.Response;
import cn.tqhweb.jweb.plugin.template.TemplatePlugin;

public abstract class Controller {

	protected App app;
	protected Request request;
	protected Response response;

	public Controller set(Map<String, Object> data) {
		for (Entry<String, Object> entry : data.entrySet()) {
			set(entry.getKey(), entry.getValue());
		}
		return this;
	}
	public Controller set(String key, Object value) {
		request.setAttribute(key, value);
		return this;
	}

	public final Response fetch(String view) {
		TemplatePlugin template = (TemplatePlugin) app.getConfig().getPlugins().get("template");
		return template.fetch(request, response, view);
	}
	
	public final Response fetch(String view, Map<String, Object> data) {
		return set(data).fetch(view);
	}

	public final Response json(Object jsonObject) {
		response.sendJSON(jsonObject);
		return response;
	}

	public final Response redirect(String uri) {
		response.sendRedirect(uri);
		return response;
	}

	public final Response redirect(String uri, int stateCode) {
		response.sendRedirect(uri, stateCode);
		return response;
	}

	public Controller() {
		app = App.getApp();
		request = app.getRequest();
		response = app.getResponse();
	}
}
