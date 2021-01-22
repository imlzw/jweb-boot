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

package cc.jweb.boot.utils.http.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class MyCookieJar implements CookieJar{

	//Cookie缓存区
	private final Map<String, List<Cookie>> cookiesMap = new HashMap<String, List<Cookie>>();
	@Override
	public void saveFromResponse(HttpUrl arg0, List<Cookie> arg1) {
		//移除相同的url的Cookie
		String host = arg0.host();
		List<Cookie> cookiesList = cookiesMap.get(host);
		if (cookiesList != null){
			cookiesMap.remove(host);
		}
		//再重新天添加
		cookiesMap.put(host, arg1);
	}
	
	@Override
	public List<Cookie> loadForRequest(HttpUrl arg0) {
		List<Cookie> cookiesList = cookiesMap.get(arg0.host());
		//注：这里不能返回null，否则会报NULLException的错误。
		//原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
		return cookiesList != null ? cookiesList : new ArrayList<Cookie>();
	}
	
}
