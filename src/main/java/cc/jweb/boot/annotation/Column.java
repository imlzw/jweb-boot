/*
 * Copyright  (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
