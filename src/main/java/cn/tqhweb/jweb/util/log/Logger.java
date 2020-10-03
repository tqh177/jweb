package cn.tqhweb.jweb.util.log;

public interface Logger {
	public static Logger factory(Class<?> clazz) {
		switch (LoggerFactory.getLogType()) {
		case LoggerFactory.log4j:
			return new Log4j(clazz);
		case LoggerFactory.log4j2:
			throw new RuntimeException("无法创建log4j2");
		case LoggerFactory.jdkLog:
			throw new RuntimeException("无法创建jdkLog");
		default:
			throw new RuntimeException("无法创建logger");
		}
	}

	public static Logger factory(Class<?> clazz, Level level) {
		Logger logger = factory(clazz);
		logger.setLevel(level);
		return logger;
	}

	public static Logger getGlobal() {
		return factory(Logger.class);
	}

	public void setLevel(Level level);

	public void setLevel(int level);

	public void debug(Object message);

	public void debug(Object message, Throwable t);

	public void debug(String format, Object... args);

	public void info(Object message);

	public void info(Object message, Throwable t);

	public void info(String format, Object... args);

	public void warn(Object message);

	public void warn(Object message, Throwable t);

	public void warn(String format, Object... args);

	public void error(Object message);

	public void error(Object message, Throwable t);

	public void error(String format, Object... args);

	public void fatal(Object message);

	public void fatal(Object message, Throwable t);

	public void fatal(String format, Object... args);

	public void trace(Object message);

	public void trace(Object message, Throwable t);

	public void trace(String format, Object... args);
}