package cn.tqhweb.jweb.plugin.cache;

import cn.tqhweb.jweb.plugin.Plugin;

public abstract class CachePlugin implements Plugin {
	private static CachePlugin cachePlugin;
	
	public static CachePlugin getCachePlugin() {
		return cachePlugin;
	}
	public static void regist(CachePlugin cachePlugin) {
		CachePlugin.cachePlugin = cachePlugin;
	}
	public boolean contain(String name, Object key) {
		return get(name, key) == null;
	}
	public abstract void set(String name, Object key, Object value);
	public abstract Object get(String name, Object key);
//	public abstract long getTime(String name, Object key);
	public abstract void remove(String name, Object key);
}
