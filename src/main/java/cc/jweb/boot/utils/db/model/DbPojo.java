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
public class DbPojo {

	public final static String TYPE_MYSQL ="MySQL";
	public final static String TYPE_ORACLE ="Oracle";
	public final static String TYPE_SQLITE = "SQLite";
	
	public String name; 		//用以获得当前数据库是什么数据库。比如oracle，access等。
	public String version; 	//获得数据库的版本。
	public String driverVersion; 	//获得驱动程序的版本。
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDriverVersion() {
		return driverVersion;
	}
	public void setDriverVersion(String driverVersion) {
		this.driverVersion = driverVersion;
	}
	
}
