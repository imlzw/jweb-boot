package cc.jweb.boot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {

	
	public enum TrimMode {
		/**
		 * 去除空格模式
		 * default 默认不做处理
		 * all     可以替换大部分空白字符， 不限于空格  \s 可以匹配空格、制表符、换页符等空白字符的其中任意一个
		 * blank   去除所有空格
		 * lr      去除首尾空格
		 * l       去除左空格
		 * r       去除右空格
		 * @return
		 */
		Default,All,Blank,Lr,L,R
	}
	
	public enum FilterMode {
		Default,Replace,Escape,Script
	}
	/**
	 * 长度模式
	 * @author LinCH
	 *
	 */
	public enum LengthMode{
		Default,Byte
	}
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
	String text();
	/**
	 * 参数的key
	 * <p>允许以map.name的形式出现，支持扩展map对象的取值</p>
	 * @return
	 */
	String key() default "";
	/**
	 * 实际的参数名称
	 * <p>实际的参数名称，取值为key.split("[.]")[key.split("[.]").length-1]</p>
	 * @return
	 */
	String name();
	/**
	 * 参数类型
	 * <p>默认为字符串类型</p>
	 * @return
	 */
	Class<?> clazz() default String.class;
	/**
	 * 是否必须
	 * @return
	 */
	boolean required() default false;
	
	/**
	 * 最小字符长度，一个汉字算两个
	 * @return
	 */
	int minLength() default -1;
	/**
	 * 最大字符长度，一个汉字算两个
	 * @return
	 */
	int maxLength() default -1;
	/**
	 * 长度计算模式
	 * @return
	 */
	LengthMode lengthMode() default LengthMode.Default;
	/**
	 * 非法字符串
	 * @return
	 */
	String[] illegalString() default {};
	/**
	 * 正则表达式
	 */
	Regex regex() default @Regex();
	/**
	 * 过滤模式
	 * default 默认不过滤
	 * replace 替换尖括号
	 * escape  转换编码
	 * script  替换javascript脚本
	 * @return
	 */
	FilterMode filterMode() default FilterMode.Default;
	/**
	 * 去除空格模式
	 * default 默认不做处理
	 * all     可以替换大部分空白字符， 不限于空格  \s 可以匹配空格、制表符、换页符等空白字符的其中任意一个
	 * blank   去除所有空格
	 * lr      去除首尾空格
	 * l       去除左空格
	 * r       去除右空格
	 * @return
	 */
	TrimMode trimMode() default TrimMode.Default;
	/**
	 * 日期格式转换格式
	 * @return
	 */
	String dateFormat() default "yyyy-MM-dd HH:mm:ss";
}
