package cn.tqhweb.jweb.config;

import java.util.HashMap;
import java.util.Map;

public class Constant {
	boolean isDebug = false;

	String templatePath = "/";
	String encodeFormat = "utf-8";
	String cacheType = "file";
	boolean useCglib = true;
	final Map<String, Object> global = new HashMap<>();

	public void setGlobal(String key, Object object) {
		global.put(key, object);
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public void setEncodeFormat(String encodeFormat) {
		this.encodeFormat = encodeFormat;
	}

	public void setCache(String cache) {
		this.cacheType = cache;
	}
}
