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
package cc.jweb.boot.security.annotation;

import java.lang.annotation.*;

/**
 * 用来清除所有的jweb security 访问控制注解
 * 适合于Controller绝大部分方法都需要做认证与权限访问控制，
 * 个别不需要做访问控制的场合。
 * 仅能用在方法上。
 *
 * @author imlzw
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ClearSecurity {
}
