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

package cc.jweb.boot.utils.db.model;


/**
 * 数据库字段信息存放pojo
 * 
 * @author ag777
 * @version 
 */
public class TypePojo {

	private String field;
	private String type;
	private Boolean nullAble;
	private String extra;
	private String key;
	
	public String getField() {
		return field;
	}
	public TypePojo setField(String field) {
		this.field = field;
		return this;
	}
	public String getType() {
		return type;
	}
	public TypePojo setType(String type) {
		this.type = type;
		return this;
	}
	public Boolean getNullAble() {
		return nullAble;
	}
	public TypePojo setNullAble(Boolean nullAble) {
		this.nullAble = nullAble;
		return this;
	}
	public String getExtra() {
		return extra;
	}
	public TypePojo setExtra(String extra) {
		this.extra = extra;
		return this;
	}
	public String getKey() {
		return key;
	}
	public TypePojo setKey(String key) {
		this.key = key;
		return this;
	}
	
}
