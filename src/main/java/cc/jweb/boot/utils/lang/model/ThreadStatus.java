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

package cc.jweb.boot.utils.lang.model;

/**
 * 线程状态枚举类,配合<code>BasePeriodicTask</code>做任务状态控制。
 * 
 * @author ag777
 * @version create on 2018年01月08日,last modify at 2017年01月08日
 */
public enum ThreadStatus {

	prepare {
		@Override
		public String toString() {
			return "未开始";
		}
	},
	toPause {
		@Override
		public String toString() {
			return "暂停中";
		}
	},
	pause {
		@Override
		public String toString() {
			return "暂停";
		}
	},
	resume {
		@Override
		public String toString() {
			return "正常运行";
		}
	},
	toStop {
		@Override
		public String toString() {
			return "停止中";
		}
	},
	stop {
		@Override
		public String toString() {
			return "停止";
		}
	};
	
	public String toString() {
		return "";
	}
}
