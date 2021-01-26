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
package cc.jweb.boot.security.processer;


import cc.jweb.boot.security.exception.AuthorizationException;

import java.util.List;

/**
 * 组合模式访问控制处理器
 * @author dafei
 *
 */
public class CompositeAuthzProcesser implements AuthzProcesser {

	private final List<AuthzProcesser> authzProcessers;

	public CompositeAuthzProcesser(List<AuthzProcesser> authzProcessers){
		this.authzProcessers = authzProcessers;
	}

	public void assertAuthorized() throws AuthorizationException {
		for(AuthzProcesser authzProcesser : authzProcessers){
			authzProcesser.assertAuthorized();
		}
	}
}
