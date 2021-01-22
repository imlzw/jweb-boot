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

package cc.jweb.boot.utils.lang.exception.model;

/**
 * 除数为0异常
 * @author ag777
 *
 * @version create on 2018年07月19日,last modify at 2018年07月19日
 */
public class DivisorIsZeroException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 188844524372497557L;

	public DivisorIsZeroException() {
		super("除数不能为0");
	}
	
	public DivisorIsZeroException(String message) {
		super(message);
	}
}
