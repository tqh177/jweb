package cn.tqhweb.jweb.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import cn.tqhweb.jweb.aop.interceptor.BeforeInterceptor;
import cn.tqhweb.jweb.aop.interceptor.RequestMethodInterceptor;
import cn.tqhweb.jweb.aop.proxy.ProxyFactory;
import cn.tqhweb.jweb.config.route.Route;
import cn.tqhweb.jweb.plugin.Plugin;
import cn.tqhweb.jweb.plugin.datasource.DataSourcePlugin;
import cn.tqhweb.jweb.plugin.template.JspPlugin;

public abstract class Config {
	private static Config config;

	public static Config getConfig() {
		return config;
	}

	private static void setConfig(Config config) {
		Config.config = config;
	}

	private Interceptors interceptors = new Interceptors();
	private Route route = new Route();
	private Plugins plugins = new Plugins();
	private ProxyFactory proxyFactory;

	private Constant constant;
	private ServletContext context;
	private Map<String, Object> map = new HashMap<>();

	protected abstract void route(Route route);

	protected abstract void intercept(Interceptors interceptors);

	protected abstract void plugin(Plugins plugins);

	protected abstract void constant(Constant constant);

	// 用户自定义配置
	protected abstract void addCustomerConfig(Map<String, Object> map);

	public void init(FilterConfig filterConfig) {
		setConfig(this);
		context = filterConfig.getServletContext();
		constant = new Constant();
		// 常量定义在前
		constant(constant);

		// 自定义配置
		addCustomerConfig(map);

		// 创建代理工厂
		proxyFactory = new ProxyFactory(this);

		route(route);
		plugin(plugins);
		if (plugins.get("template") == null) {
			plugins.register("template", new JspPlugin(this));
		}
		// 注意添加前后顺序
		interceptors.add(new RequestMethodInterceptor());
		intercept(interceptors);
		interceptors.add(new BeforeInterceptor());
	}

	public void destroy() {
		for (Entry<String, Plugin> entry : plugins.get().entrySet()) {
			entry.getValue().stop();
		}
	}

	public Object getCustomerConfig(String key) {
		return map.get(key);
	}

	public Plugins getPlugins() {
		return plugins;
	}

	@Deprecated
	public DataSource getDataSource() {
		return DataSourcePlugin.getDataSource();
	}

	public String getCache() {
		return constant.cacheType;
	}

	public String getTemplatePath() {
		return constant.templatePath;
	}

	public String getEncodeFormat() {
		return constant.encodeFormat;
	}

	public boolean isDebug() {
		return constant.isDebug;
	}

	public Map<String, Object> getGlobal() {
		return constant.global;
	}

	public Interceptors getInterceptors() {
		return interceptors;
	}

	public Route getRoute() {
		return route;
	}

	public ServletContext getContext() {
		return context;
	}

	public ProxyFactory getProxyFactory() {
		return proxyFactory;
	}

	public boolean isUseCglib() {
		return constant.useCglib;
	}

	public void setUseCglib(boolean useCglib) {
		constant.useCglib = useCglib;
	}
}
