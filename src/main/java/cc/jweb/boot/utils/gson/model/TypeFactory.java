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

package cc.jweb.boot.utils.gson.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 便捷地获取带泛型类的Type
 * <p>
 * 比如:
 * <pre>{@code
 * new TypeFactory(List.class, String.class) => List<String>
 * new TypeFactory(Map.class, String.class, Object.class) => Map<String, Object>
 * }</pre>
 * 
 * @author ag777
 * @version create on 2018年05月16日,last modify at 2019年04月22日
 */
public class TypeFactory implements ParameterizedType {

	private Type rawClass;
	private Type[] argumentsType;

	public TypeFactory(Class<?> rawClass, Class<?>... argumentsClass) {
		this.rawClass = rawClass;
		this.argumentsType = argumentsClass;
	}
	
	public TypeFactory(Class<?> rawClass, Type... argumentsType) {
		this.rawClass = rawClass;
		this.argumentsType = argumentsType;
	}

	@Override
	public Type[] getActualTypeArguments() {
		return argumentsType;
	}

	@Override
	public Type getOwnerType() {
		return null;
	}

	@Override
	public Type getRawType() {
		return rawClass;
	}
}
