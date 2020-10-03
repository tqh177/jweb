package cn.tqhweb.jweb.util.log;

public enum Level {
    OFF,
    FATAL,
    ERROR,
    WARN,
    INFO,
    DEBUG,
    TRACE,
    ALL;
    public static Level valueOf(int i){
        return values()[i];
    }
}