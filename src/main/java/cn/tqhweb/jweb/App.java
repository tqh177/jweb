package cn.tqhweb.jweb;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.tqhweb.jweb.aop.Aop;
import cn.tqhweb.jweb.aop.Invocation;
import cn.tqhweb.jweb.config.Config;
import cn.tqhweb.jweb.exception.HttpException;
import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.http.Response;
import cn.tqhweb.jweb.plugin.datasource.DataSourcePlugin;
import cn.tqhweb.jweb.util.log.Logger;

public class App {
	static private Logger logger = Logger.factory(App.class);

	static private ThreadLocal<App> app = new ThreadLocal<App>();

	private Config config;
	private Request request;
	private Response response;
	private Connection connection;
	private List<Runnable> finishRequestList = new ArrayList<Runnable>();

	App(HttpServletRequest req, HttpServletResponse res, Config config) {
		this.request = Request.wrap(req, this);
		this.response = Response.wrap(res, this);
		this.config = config;
		app.set(this);
	}

	/**
	 * 获取当前线程全局App实例
	 * 
	 * @return
	 */
	static public App getApp() {
		return app.get();
	}

	/** 返回全局配置 */
	public Config getConfig() {
		return config;
	}

	public void dispatch(Method method, Class<?> clazz) throws ServletException, IOException {
		try {
			// 触发方法
			Invocation invocation = new Invocation(clazz, method);
			// invocation.setArgs(Aop.getMethodArgs(method, this));
			invocation.setTarget(Aop.inject(clazz.getConstructor().newInstance(), this));
			invocation.setContext(this);
			invocation.setArgs(Aop.getMethodArgs(method, this));
			invocation.setInterceptors(getConfig().getInterceptors().iterator());
			Object obj = invocation.invoke();
			if (obj == null) {
				throw new HttpException(404);
			} else if (obj instanceof Response) {
				// DO NOTHING
			} else if (obj instanceof String) {
				response.sendText((String) obj);
			} else if (obj instanceof char[]) {
				response.sendChars((char[]) obj);
			} else if (obj.getClass().getName().startsWith("java")) {
				response.sendText(obj.toString());
			} else {
				// 发送JSON字符串
				response.sendJSON(obj);
			}
			response.send();
		} catch (ReflectiveOperationException e) {
			// 反射出错，404
			logger.info("Can not dispatch the action: [%s]", clazz.getSimpleName() + "." + method.getName());
//			e.printStackTrace();
			throw new HttpException(404);
		}
	}

	void dispatch(Method method) throws ServletException, IOException {
		dispatch(method, method.getDeclaringClass());
	}

	private long startTime;

	/** 处理新请求 */
	void newRequestStart() throws IOException, ServletException {
		startTime = System.currentTimeMillis();
		logger.info("接受请求: " + request.getRequestURI());
		request.setCharacterEncoding(config.getEncodeFormat());
		response.setCharacterEncoding(config.getEncodeFormat());
		Method method = config.getRoute().route(request);
		if (method == null) {
			throw new HttpException(404);
		}
		dispatch(method);
	}

	/** 处理新请求结束 */
	void newRequestEnd() throws IOException {
		for (Runnable runnable : finishRequestList) {
			runnable.run();
		}
		logger.debug("发送响应完成: %s", request.getRequestURI());
		logger.info("本次请求'%s'花费 [%d]ms", request.getRequestURI(), System.currentTimeMillis() - startTime);
		closeConnection();
		logger.info("app destroy\n");
		request = null;
		response = null;
		connection = null;
		finishRequestList = null;
		app.remove();
	}

	/** 注册完成请求后执行的方法 */
	public void registerFinishRequest(Runnable runnable) {
		finishRequestList.add(runnable);
	}

	/**
	 * @return the request
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * @return the response
	 */
	public Response getResponse() {
		return response;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		if (connection == null) {
			try {
				connection = DataSourcePlugin.getConnection();
				logger.debug("获取数据库连接成功");
			} catch (Exception e) {
				logger.fatal("获取sql连接失败", e);
				throw new RuntimeException(e);
			}
		}
		return connection;
	}

	private List<String> sqls = new ArrayList<>();

	public List<String> getBatchList() throws SQLException {
		return sqls;
	}

	private void closeConnection() {
		if (connection != null) {
			try {
				try {
					if (!sqls.isEmpty()) {
						Statement statement = connection.createStatement();
						connection.setAutoCommit(false);
						for (String sql : sqls) {
							statement.addBatch(sql);
							logger.info("[batch SQL]=>" + sql);
						}
						statement.executeBatch();
						connection.commit();
						statement.close();
						connection.setAutoCommit(true);
						logger.debug("批处理完成");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				connection.close();
				logger.debug("数据库连接关闭成功");
			} catch (SQLException e) {
				logger.debug("数据库连接关闭失败", e);
			}
		}
	}
}
