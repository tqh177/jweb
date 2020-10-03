package cn.tqhweb.jweb.aop.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface AllowMethod {
	static int NONE = 0b0;
	static int GET = 0b1;
	static int POST = 0b10;
	static int PUT = 0b100;
	static int DELETE = 0b1000;
	int value() default GET|POST|PUT|DELETE;
}
