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

package cc.jweb.boot.utils.lang.interf;

/**
 * 进度监听回调接口
 * 
 * @author ag777
 * @version create on 2018年05月15日,last modify at 2018年05月15日
 */
public interface ProgressListener {

	/**
	 * 
	 * @param cur 已读取字节数
	 * @param total 总字节数
	 * @param done 是否完成
	 */
	public void update(int cur, int total, boolean done);
}
