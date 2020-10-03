package cn.tqhweb.app;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import cn.tqhweb.jweb.aop.interceptor.CacheInterceptor;
import cn.tqhweb.jweb.config.Constant;
import cn.tqhweb.jweb.config.Interceptors;
import cn.tqhweb.jweb.config.Plugins;
import cn.tqhweb.jweb.config.route.Route;
import cn.tqhweb.jweb.plugin.cache.EhCachePlugin;
import cn.tqhweb.jweb.plugin.datasource.Druid;
import cn.tqhweb.jweb.plugin.session.EhCacheSessionPlugin;
import cn.tqhweb.jweb.plugin.session.SessionPlugin;
import cn.tqhweb.jweb.plugin.template.FreemakerPlugin;
import freemarker.template.Configuration;

public class Config extends cn.tqhweb.jweb.config.Config {

	@Override
	protected void route(Route route) {
		route.autoBind("cn.tqhweb.app.controller");
	}

	@Override
	protected void intercept(Interceptors interceptors) {
		interceptors.add(new CacheInterceptor());
	}

	@Override
	protected void constant(Constant constant) {
		constant.setDebug(true);
		constant.setTemplatePath("/WEB-INF/tpl/");
	}

	@Override
	protected void plugin(Plugins plugins) {
		plugins.register("datasource", Druid.getInstance());
		FreemakerPlugin freemakerPlugin = new FreemakerPlugin(this);
		plugins.register("template", freemakerPlugin);
		Configuration configuration = freemakerPlugin.getConfiguration();
		configuration.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
		EhCachePlugin cachePlugin = EhCachePlugin.getInstance();
		plugins.register("cache", cachePlugin);
		SessionPlugin sessionPlugin = new EhCacheSessionPlugin();
		plugins.register("session", sessionPlugin);
	}

	@Override
	protected void addCustomerConfig(Map<String, Object> map) {
		Properties properties = new Properties();
		try {
			properties.load(Config.class.getClassLoader().getResourceAsStream("config.properties"));
			String status = properties.getProperty("app.status");
			if (status != null) {
				String path = status + ".properties";
				properties.load(Config.class.getClassLoader().getResourceAsStream(path));
			}
			for (String key : properties.stringPropertyNames()) {
				if (key.startsWith("app.")) {
					continue;
				} else if (key.startsWith("global.")) {
					getGlobal().put(key.substring(7), properties.getProperty(key));
				} else {
					map.put(key, properties.getProperty(key));
				}
			}
		} catch (IOException e) {
			System.out.println("提醒:配置文件加载失败");
			throw new RuntimeException(e);
		}
	}
}
