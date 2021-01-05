package cc.jweb.boot.annotation;

import java.lang.annotation.*;

@Documented  
@Inherited  
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)
public @interface Column {

	/**
	 * 列名
	 * <p>数据库中的字段名称，通常是下划线分隔</p>
	 * @return
	 */
	String field() default "";
	/**
	 * 参数的（中文）名称
	 * @return
	 */
	String text() default "";
	/**
	 * 日期格式转换格式
	 * @return
	 */
	String dateFormat() default "yyyy-MM-dd HH:mm:ss";
}
