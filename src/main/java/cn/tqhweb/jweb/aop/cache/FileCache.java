package cn.tqhweb.jweb.aop.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cn.tqhweb.jweb.App;
import cn.tqhweb.jweb.util.log.Logger;

class FileCache extends CacheAbstract {

	public static String basePath = "/runtime2/";
	private final File file;
	private static Logger logger = Logger.factory(FileCache.class);

	static {
		File file = new File(App.getApp().getConfig().getContext().getRealPath(basePath));
		if (!file.isDirectory()) {
			file.mkdir();
		}
	}

	FileCache(String key) {
		super(key);
		file = new File(App.getApp().getConfig().getContext().getRealPath(basePath + this.key));
		if (file.isFile()) {
			lastModified = file.lastModified();
			long expiredTime = lastModified + defaultExpiredTime;
			if (expiredTime < System.currentTimeMillis()) {
				delete();
				lastModified = 0;
			} else {
				try {
					synchronized (file.getName().intern()) {
						logger.debug("缓存读取");
						BufferedReader reader = new BufferedReader(new FileReader(file));
						CharArrayWriter writer = new CharArrayWriter();
						int c;
						while ((c = reader.read()) != -1) {
							writer.write(c);
						}
						reader.close();
						buf = writer.toCharArray();
						writer.close();
						logger.debug("缓存读取完成");
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public boolean delete() {
		logger.debug("删除缓存");
		return file.delete();
	}

	@Override
	public boolean save() {
		if (!exist()) {
			return false;
		}
		try {
			synchronized (file.getName().intern()) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.write(buf);
				writer.flush();
				writer.close();
				logger.debug("缓存保存完成");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
