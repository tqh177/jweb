package cn.tqhweb.jweb.config;

import java.util.HashMap;
import java.util.Map;

import cn.tqhweb.jweb.plugin.Plugin;
import cn.tqhweb.jweb.util.log.Logger;

public class Plugins {
	private Map<String, Plugin> plugins = new HashMap<>();
	private static Logger logger = Logger.factory(Plugins.class);

	public void register(String name, Plugin plugin) {
		if (!plugin.start()) {
			throw new RuntimeException(String.format("plugin %s register fail", name));
		}
		plugins.putIfAbsent(name, plugin);
		logger.debug("plugin %s register success", name);
	}

	public Plugin get(String name) {
		return plugins.get(name);
	}
	
	Map<String, Plugin> get() {
		return plugins;
	}
}
