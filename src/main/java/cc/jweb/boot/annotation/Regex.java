package cc.jweb.boot.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Regex {
	/**
	 * 规则，默认自定义
	 * @return
	 */
	String rule() default "custom";
	/**
	 * 错误消息提示
	 * @return
	 */
	String message() default "验证失败";
	/**
	 * 表达式
	 * @return
	 */
	String expression() default "";
}
