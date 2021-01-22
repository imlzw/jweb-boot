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
 * 验证异常(验证参数过程，判断出的异常)
 * @author ag777
 *
 * @version create on 2018年05月17日,last modify at 2020年04月24日
 */
public class ValidateException extends Exception {

	private static final long serialVersionUID = -1358796764058245553L;

	private String extraMsg;	//拓展信息
	
	public String getExtraMsg() {
		return extraMsg;
	}
	
	public ValidateException(String message){
		super(message);
	}
	
	public ValidateException(String message, String extraMsg){
		super(message);
		this.extraMsg = extraMsg;
	}
	
	public ValidateException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public ValidateException(String message, String extraMsg, Throwable cause) {
        super(message, cause);
        this.extraMsg = extraMsg;
    }

}
