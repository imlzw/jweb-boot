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
 * 代表<code>META-INF/services/</code>中的文件未找到或读文件失败的异常。
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月14日 下午2:17:28
 */
public class ServiceNotFoundException extends ClassNotFoundException {
    /**
	 * 
	 */
    private static final long serialVersionUID = -6525751787646334079L;

    /**
     * 构造一个空的异常.
     */
    public ServiceNotFoundException() {
        super();
    }

    /**
     * 构造一个异常, 指明异常的详细信息.
     * 
     * @param message 详细信息
     */
    public ServiceNotFoundException(String message) {
        super(message);
    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param cause 异常的起因
     */
    public ServiceNotFoundException(Throwable cause) {
        super(null, cause);

    }

    /**
     * 构造一个异常, 指明引起这个异常的起因.
     * 
     * @param message 详细信息
     * @param cause 异常的起因
     */
    public ServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
