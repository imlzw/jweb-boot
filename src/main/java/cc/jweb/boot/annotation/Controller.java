/*
 * Copyright (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
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
