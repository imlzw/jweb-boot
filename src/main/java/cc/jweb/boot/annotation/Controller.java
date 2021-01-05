package cc.jweb.boot.annotation;


import java.lang.annotation.*;

/**
 * Jfinal Controller 扫描注解类
 * @author LinCH
 * @version 1.0
 * Date 2017/06/23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {

	/**
	 * 访问的URI
	 * @param uri 访问的URI，必填
	 * @return
	 */
	String uri();
	/**
	 * 视图路径
	 * @param viewPath 访问视图的根目录
	 * @return
	 */
	String viewPath() default "";

}
