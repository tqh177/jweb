package cn.tqhweb.jweb;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.tqhweb.jweb.config.Config;
import cn.tqhweb.jweb.exception.HttpException;
import cn.tqhweb.jweb.util.log.Logger;

//@WebFilter(initParams = { @WebInitParam(name = "configClass", value = "cn.tqhweb.app.Config")}, urlPatterns = "/*")
public class StartFilter implements Filter {

	private static Logger logger = Logger.factory(StartFilter.class);
	private Config config;

	@Override
	public void init(FilterConfig config) throws ServletException {
		long time = System.currentTimeMillis();
		logger.info("程序开始初始化");
		String className = config.getInitParameter("configClass");
		if (className == null) {
			logger.fatal("configClass没有设置");
			System.exit(1);
		}
		logger.debug("加载配置类");
		try {
			this.config = (Config) Class.forName(className).getDeclaredConstructor().newInstance();
			this.config.init(config);
		} catch (ReflectiveOperationException e) {
			logger.fatal("没有找到Config类");
			System.exit(1);
		}
		logger.debug("程序初始化完成 总耗时[%d]ms", System.currentTimeMillis() - time);
	}

	@Override
	public void destroy() {
		config.destroy();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		request.getRequestDispatcher(null);
		ServletContext context = request.getServletContext();
		String fn = ((HttpServletRequest) request).getRequestURI();
		File file = new File(context.getRealPath(fn));
		if (file.isFile()) {
			chain.doFilter(request, response);
		} else {
			App app = new App((HttpServletRequest) request, (HttpServletResponse) response, config);
			try {
				app.newRequestStart();
			} catch (HttpException e) {
				if (e.getStateCode() == 404) {
					chain.doFilter(request, response);
				} else {
					app.getResponse().sendError(e.getStateCode());
				}
//				if (config.isDebug()) {
//					e.printStackTrace();
//				}
//				app.getResponse().sendError(e.getStateCode());
			} finally {
				// 防止内存泄漏
				app.newRequestEnd();
			}
		}
	}
}
