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

package cc.jweb.boot.utils.lang;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 有关<code>Stream</code>操作工具类
 * <p>
 * jdk8及以上
 * </p>
 * 
 * @author ag777
 * @version create on 2018年05月17日,last modify at 2018年05月17日
 */
public class StreamUtils {

	private StreamUtils() {}
	
	public static <T>List<T> toList(Stream<T> stream) {
		return stream.collect(Collectors.toList());
	}
}
