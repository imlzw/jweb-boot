package cc.jweb.boot.annotation;


import java.lang.annotation.*;

/**
 * Jfinal Model 扫描注解类
 * @author LinCH
 * @version 1.0
 * Date 2017/06/23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Model {

	String ds() default "";

	String table();
	
	String key() default "";
	
	/* 数据库类型 */
	String dbType() default "mysql";
}
