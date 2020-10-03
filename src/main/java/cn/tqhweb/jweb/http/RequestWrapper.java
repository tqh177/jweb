package cn.tqhweb.jweb.http;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import cn.tqhweb.jweb.App;
import cn.tqhweb.jweb.plugin.session.SessionPlugin;
import cn.tqhweb.jweb.util.StrKit;

class RequestWrapper extends HttpServletRequestWrapper implements Request {

	private String suffix;
	private String uri;
	private String uriNoSuffix;
	private App app;

	RequestWrapper(HttpServletRequest request, App app) {
		super(request);
		this.app = app;
		parseUri(getRequestURI());
	}

	/**
	 * 解析uri
	 * 
	 * @param uri
	 */
	private void parseUri(String uri) {
		for (int i = uri.length() - 1; i > 0; i--) {
			char c = uri.charAt(i);
			if (c == '/') {
				break;
			}
			if (c == '.') {
				suffix = uri.substring(i + 1);
				if (suffix.isEmpty()) {
					break;
				}
				uriNoSuffix = uri.substring(0, i);
				return;
			}
		}
		suffix = "";
		uriNoSuffix = uri;
	}

	/**
	 * 获取后缀
	 * 
	 * @return the suffix
	 */
	@Override
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @return the uriNoSuffix
	 */
	@Override
	public String getUriNoSuffix() {
		return uriNoSuffix;
	}

	@Override
	public String getRequestURI() {
		if (uri == null) {
			return uri = super.getRequestURI();
		}
		return uri;
	}

	private Map<String, String> param = new HashMap<>();

	@Override
	public void addParam(String key, String value) {
		this.param.put(key, value);
	}

	@Override
	public void addParam(Map<String, String> map) {
		param.putAll(map);
	}

	/**
	 * 根据名称获取Cookie
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Cookie getCookie(String key) {
		Cookie[] cookies = getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(key)) {
					return cookie;
				}
			}
		}
		return null;
	}

	HttpSession session;

	@Override
	public HttpSession getSession(boolean create) {
		if (session != null) {
			return session;
		}
		session = SessionPlugin.getSession(this, app.getResponse(), create);
		if (session == null) {
			return super.getSession(create);
		}
		return session;
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	@Override
	public String getRequestedSessionId() {
		String name = SessionPlugin.getSessionName();
		if (name == null) {
			return super.getRequestedSessionId();
		}
		Cookie cookie = getCookie(name);
		if (cookie == null) {
			return null;
		}
		return cookie.getValue();
	}

	@Override
	public Object getSessionAttribute(String key) {
		HttpSession session = getSession(false);
		if (session == null) {
			return null;
		}
		return session.getAttribute(key);
	}

	@Override
	public boolean isAjax() {
		return "XMLHttpRequest".equals(getHeader("x-requested-with"));
	}

	/**
	 * 获取指定get|post|...等参数(String类型)
	 * 
	 * @return
	 */
	@Override
	public String get(String key) {
		String value = param.get(key);
		if (value == null) {
			value = getParameter(key);
		}
		return value;
	}

	/**
	 * 获取指定get|post|...等参数(Integer类型)
	 * 
	 * @param i
	 * @return
	 */
	@Override
	public Integer getInt(String key) {
		return StrKit.toInt(get(key));
	}

	/**
	 * 获取指定get|post|...等参数(Long类型)
	 * 
	 * @param i
	 * @return
	 */
	@Override
	public Long getLong(String key) {
		return StrKit.toLong(get(key));
	}

	/**
	 * 获取指定get|post|...等参数(Boolean类型)
	 * 
	 * @param i
	 * @return
	 */
	@Override
	public Boolean getBoolean(String key) {
		return StrKit.toBoolean(get(key));
	}
}
