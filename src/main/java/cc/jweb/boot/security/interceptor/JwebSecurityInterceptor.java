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

package cc.jweb.boot.security.interceptor;

import cc.jweb.boot.security.session.JwebSecuritySession;
import cc.jweb.boot.security.utils.JwebSecurityUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import org.apache.shiro.SecurityUtils;

public class JwebSecurityInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        try {
            inv.invoke();
        }finally {
            JwebSecuritySession session = JwebSecurityUtils.getSession();
            if (session != null) {
                session.postIntercept();
            }
        }
    }
}
