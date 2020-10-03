package cn.tqhweb.jweb.http;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.tqhweb.jweb.App;

import javax.servlet.http.Cookie;
public interface Request extends HttpServletRequest {
	public static Request wrap(HttpServletRequest request, App app) {
		return new RequestWrapper(request, app);
	}
	public String getSuffix();
	public String getUriNoSuffix();
	public Cookie getCookie(String key);
	public Object getSessionAttribute(String key);
	
	public boolean isAjax();

	public void addParam(String key, String value);
	public void addParam(Map<String, String> map);
	/** 获取指定get|post|...等参数(String类型) */
	public String get(String key);

	/** 获取指定get|post|...等参数(Integer类型) */
	public Integer getInt(String key);

	/** 获取指定get|post|...等参数(Long类型) */
	public Long getLong(String key);

	/** 获取指定get|post|...等参数(Boolean类型) */
	public Boolean getBoolean(String key);
}
