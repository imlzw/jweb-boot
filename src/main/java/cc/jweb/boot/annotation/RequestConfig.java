package cc.jweb.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数注解
 * @author LinCH
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestConfig{
	
	/**
	 * 取值模式
	 * @author LinCH
	 *
	 */
	public enum ValueMode {
		Default,RequestBody
	}
	/**
	 * 是否记录日志
	 * @return
	 */
	boolean logger() default true;
	/**
	 * 是否启用token
	 * @return
	 */
	boolean token() default false;
	/**
	 * 取值模式
	 * requestBody
	 * form
	 */
	ValueMode valueMode() default ValueMode.Default;
}
