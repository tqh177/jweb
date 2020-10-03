package cn.tqhweb.jweb.plugin.session;

import javax.servlet.http.HttpSession;

import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.http.Response;
import cn.tqhweb.jweb.plugin.Plugin;

public abstract class SessionPlugin implements Plugin {
	private static SessionPlugin sessionPlugin;
	public static HttpSession getSession(Request request, Response response, boolean create) {
		if (sessionPlugin == null) {
			return null;
		}
		return sessionPlugin.getSession_(request, response, create);
	}
	public static String getSessionName() {
		if (sessionPlugin == null) {
			return null;
		}
		return sessionPlugin.getSessionName_();
	}
	/**
	 * 注册一个获取Session的插件
	 * @param sessionPlugin
	 */
	protected static void register(SessionPlugin sessionPlugin) {
		SessionPlugin.sessionPlugin = sessionPlugin;
	}
	protected abstract HttpSession getSession_(Request request, Response response, boolean create);
	protected abstract String getSessionName_();
	public abstract int getTotal();
}
