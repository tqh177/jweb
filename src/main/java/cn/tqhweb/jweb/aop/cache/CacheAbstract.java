package cn.tqhweb.jweb.aop.cache;

import cn.tqhweb.jweb.util.StrKit;
import cn.tqhweb.jweb.util.log.Logger;

public abstract class CacheAbstract implements Cache {

	public static final int defaultExpiredTime = (int) 3600E3;
	protected long expiredTime;
	protected long lastModified;
	protected String key;
	protected char[] buf;
	private Logger logger = Logger.factory(CacheAbstract.class);

	protected static String hash(String s) {
		return StrKit.md5(s);
	}

	CacheAbstract(String key) {
		this.key = hash(key);
		logger.debug(this.key);
	}

	@Override
	public boolean exist() {
		return lastModified != 0;
	}

	@Override
	public long getExpiredTime() {
		return expiredTime;
	}

	@Override
	public long getLastModified() {
		return lastModified;
	}

	@Override
	public void setCharArray(char[] buf) {
		lastModified = System.currentTimeMillis();
		this.buf = buf;
	}

	@Override
	public char[] toCharArray() {
		return buf;
	}

//	public abstract long getExpiredTime();
//	public abstract void setCharArray(char[] buf);
//	public abstract char[] toCharArray();
//	public abstract long getLastModified();
//	public abstract boolean exist();
//	public abstract boolean delete();
//	public abstract boolean save();
}
