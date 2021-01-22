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

import java.util.List;

/**
 * 数据库字段信息存放pojo
 * 
 * @author ag777
 * @version 
 */
public class DBIPojo {

	public String name;
	public List<String> columnNameList;
	public int type;
	public String typeName;
	public boolean unique;
	
	public String getName() {
		return name;
	}
	public DBIPojo setName(String name) {
		this.name = name;
		return this;
	}
	public List<String> getColumnNameList() {
		return columnNameList;
	}
	public DBIPojo setColumnNameList(List<String> columnNameList) {
		this.columnNameList = columnNameList;
		return this;
	}
	public int getType() {
		return type;
	}
	public DBIPojo setType(int type) {
		this.type = type;
		return this;
	}
	public String getTypeName() {
		return typeName;
	}
	public DBIPojo setTypeName(String typeName) {
		this.typeName = typeName;
		return this;
	}
	public boolean isUnique() {
		return unique;
	}
	public DBIPojo setUnique(boolean unique) {
		this.unique = unique;
		return this;
	}
	
}
