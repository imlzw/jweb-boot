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

package cc.jweb.boot.utils.lang.model;

/**
 * 部分cmd命令存放类
 * 
 * @author ag777
 *	
 */
public class CmdTemplate {

	public static final String SHELL_CPU_ID = "dmidecode -t 4 | grep ID |sort -u |awk -F': ' '{print $2}'";		//获取cpu_id,一行一个,linux和麒麟系统通用
	public static final String SHELL_MAC = "ifconfig -a|grep \"HWaddr\"|awk {'print $5'}";							//获取mac地址,一行一个
	
	public static final String KYLIN_SHELL_MAC = "ifconfig -a|grep \"ether\"|awk {'print $2'}";					//获取mac地址,一行一个,麒麟系统
	
}
