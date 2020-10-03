package cn.tqhweb.jweb.aop.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import cn.tqhweb.jweb.aop.Interceptor;

/** 添加局部拦截器，顺序为数组依次遍历 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Before {
	Class<? extends Interceptor>[] value();
}
