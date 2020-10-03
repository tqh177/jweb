package cn.tqhweb.jweb.plugin.template;

import java.io.IOException;

import javax.servlet.ServletException;

import cn.tqhweb.jweb.config.Config;
import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.http.Response;
import cn.tqhweb.jweb.plugin.Plugin;

public class JspPlugin implements TemplatePlugin,Plugin {

	private Config config;

	public JspPlugin(Config config) {
		this.config = config;
	}

	@Override
	public Response fetch(Request request, Response response, String viewName) {
		try {
			request.getRequestDispatcher(config.getTemplatePath()+viewName).forward(request, response);
			return response;
		} catch (ServletException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean start() {
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}
}
