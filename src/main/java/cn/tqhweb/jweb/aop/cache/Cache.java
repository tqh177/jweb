package cn.tqhweb.jweb.aop.cache;

import cn.tqhweb.jweb.App;

public interface Cache {
	long getExpiredTime();
	void setCharArray(char[] buf);
	char[] toCharArray();
	long getLastModified();
	boolean exist();
	boolean delete();
	boolean save();
	static Cache factory(String key) {
		switch (App.getApp().getConfig().getCache()) {
			case "file":
				return new FileCache(key);
			default:
				return new FileCache(key);
		}
	}
}
