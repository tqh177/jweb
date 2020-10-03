package cn.tqhweb.jweb.util.log;

class LoggerFactory {
	private static int type = -1;
	public static final int jdkLog = 0;
	public static final int log4j = 1;
	public static final int log4j2 = 2;

	static {
		String logType = System.getProperty("jweb.logType");
		if (logType != null) {
			if (logType.equalsIgnoreCase("log4j") && tryImplementation("org.apache.log4j.Logger")) {
				type = log4j;
			} else if (logType.equalsIgnoreCase("log4j2") && tryImplementation("org.apache.logging.log4j.Logger")) {
				type = log4j2;
			} else if (logType.equalsIgnoreCase("jdkLog") && tryImplementation("java.util.logging.Logger")) {
				type = jdkLog;
			}
		} else {
			if (tryImplementation("org.apache.log4j.Logger")) {
				type = log4j;
			} else if (tryImplementation("org.apache.logging.log4j.Logger")) {
				type = log4j2;
			} else if (tryImplementation("java.util.logging.Logger")) {
				type = jdkLog;
			}
		}
	}

	private static boolean tryImplementation(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	static int getLogType() {
		return type;
	}
}
