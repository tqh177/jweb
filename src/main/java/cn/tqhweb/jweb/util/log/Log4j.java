package cn.tqhweb.jweb.util.log;

class Log4j implements Logger {

    private org.apache.log4j.Logger logger;
    Log4j(Class<?> clazz){
        logger = org.apache.log4j.LogManager.getLogger(clazz);
    }
    @Override
    public void setLevel(Level level) {
        logger.setLevel(org.apache.log4j.Level.toLevel(level.name(), org.apache.log4j.Level.DEBUG));
    }

    @Override
    public void setLevel(int level) {
        setLevel(Level.valueOf(level));
    }

    @Override
    public void debug(Object message) {
        logger.debug(message);
    }

    @Override
    public void debug(Object message, Throwable t) {
        logger.debug(message, t);
    }

    @Override
    public void debug(String format, Object... args) {
        logger.debug(String.format(format, args));
    }

    @Override
    public void info(Object message) {
        logger.info(message);
    }

    @Override
    public void info(Object message, Throwable t) {
        logger.info(message, t);
    }

    @Override
    public void info(String format, Object... args) {
        logger.info(String.format(format, args));
    }

    @Override
    public void warn(Object message) {
        logger.warn(message);
    }

    @Override
    public void warn(Object message, Throwable t) {
        logger.warn(message, t);
    }

    @Override
    public void warn(String format, Object... args) {
        logger.warn(String.format(format, args));
    }

    @Override
    public void error(Object message) {
        logger.error(message);
    }

    @Override
    public void error(Object message, Throwable t) {
        logger.error(message, t);
    }

    @Override
    public void error(String format, Object... args) {
        logger.error(String.format(format, args));
    }

    @Override
    public void fatal(Object message) {
        logger.fatal(message);
    }

    @Override
    public void fatal(Object message, Throwable t) {
        logger.fatal(message, t);
    }

    @Override
    public void fatal(String format, Object... args) {
        logger.fatal(String.format(format, args));
    }

    @Override
    public void trace(Object message) {
        logger.trace(message);
    }

    @Override
    public void trace(Object message, Throwable t) {
        logger.trace(message,t);
    }

    @Override
    public void trace(String format, Object... args) {
        logger.trace(String.format(format, args));
    }
}