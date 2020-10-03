package cn.tqhweb.jweb.plugin.cache;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EhCachePlugin extends CachePlugin {
	private static EhCachePlugin self;

	private static CacheManager cacheManager;
	private static int number = 0;
	public static synchronized EhCachePlugin getInstance() {
		if (self == null) {
			return self = new EhCachePlugin();
		}
		return self;
	}

	private EhCachePlugin() {
	}
	
	@Override
	public synchronized boolean start() {
		number++;
		if (cacheManager == null) {
			URL configurationFileURL = EhCachePlugin.class.getClassLoader().getResource("ehcache.xml");
			// 创建缓存管理器
			cacheManager = CacheManager.create(configurationFileURL);
			regist(this);
		}
		if (cacheManager == null) {
			return false;
		}
		return true;
	}

	@Override
	public synchronized boolean stop() {
		number--;
		if (number == 0) {
			// 关闭缓存管理器
			System.out.println("关闭缓存管理器");
			cacheManager.shutdown();
			cacheManager = null;
		}
		return true;
	}

	@Override
	public void set(String name, Object key, Object value) {
		Cache cache = cacheManager.getCache(name);
		cache.put(new Element(key, value));
	}

	@Override
	public Object get(String name, Object key) {
		Cache cache = cacheManager.getCache(name);
		Element element = cache.get(key);
		if (element != null) {
			return element.getObjectValue();
		}
		return null;
	}
	
//	@Override
//	public long getTime(String name, Object key) {
//		Cache cache = cacheManager.getCache(name);
//		Element element = cache.get(key);
//		if (element != null) {
//			return element.getLastAccessTime();
//		}
//		return -1;
//	}

	@Override
	public void remove(String name, Object key) {
		Cache cache = cacheManager.getCache(name);
		cache.remove(key);
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}
}
