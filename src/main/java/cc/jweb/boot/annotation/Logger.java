package cc.jweb.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logger {

	
	/**
	 * 描述
	 * @return
	 */
	String description();
	/**
	 * 成功状态
	 * @return
	 */
	boolean success() default true;
}
