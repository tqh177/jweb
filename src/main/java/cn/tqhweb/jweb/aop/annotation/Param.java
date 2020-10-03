package cn.tqhweb.jweb.aop.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ PARAMETER, FIELD })
public @interface Param {
	String[] value() default {};
	String[] session() default {};
	String[] cookie() default {};
	boolean allowNull() default true;
}
