package cn.tqhweb.jweb.plugin.template;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import cn.tqhweb.jweb.config.Config;
import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.http.Response;
import cn.tqhweb.jweb.plugin.Plugin;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

public class FreemakerPlugin implements TemplatePlugin, Plugin {

	private Config config;
	private Configuration configuration;

	public FreemakerPlugin(Config config) {
		this.config = config;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public boolean start() {
		// 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
		configuration = new Configuration(Configuration.getVersion());
		// 第二步：设置模板文件所在的路径。
		configuration.setServletContextForTemplateLoading(config.getContext(), config.getTemplatePath());
		// 第三步：设置模板文件使用的字符集
		configuration.setDefaultEncoding(config.getEncodeFormat());
		configuration.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
		try {
			configuration.setSharedVaribles(config.getGlobal());
		} catch (TemplateModelException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean stop() {
		configuration = null;
		return true;
	}

	@Override
	public Response fetch(Request request, Response response, String viewName) {
		try {
			Template template = configuration.getTemplate(viewName);
			Map<String, Object> dataModel = new HashMap<>();
			Enumeration<String> enumeration = request.getAttributeNames();
			while (enumeration.hasMoreElements()) {
				String key = enumeration.nextElement();
				Object value = request.getAttribute(key);
				dataModel.put(key, value);
			}
			try {
				response.setContentType(Response.HTML_CONTENT_TYPE);
				template.process(dataModel, response.getWriter());
				return response;
			} catch (TemplateException e) {
				throw new RuntimeException(e);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
