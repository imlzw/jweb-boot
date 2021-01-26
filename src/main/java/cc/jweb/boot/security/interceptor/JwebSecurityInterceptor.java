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

package cc.jweb.boot.security.interceptor;

import cc.jweb.boot.security.config.JwebSecurityConfig;
import cc.jweb.boot.security.exception.AuthorizationException;
import cc.jweb.boot.security.exception.UnauthenticatedException;
import cc.jweb.boot.security.processer.AuthzProcesser;
import cc.jweb.boot.security.session.JwebSecuritySession;
import cc.jweb.boot.security.utils.JwebSecurityUtils;
import cc.jweb.boot.security.utils.JwebUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import java.io.IOException;

public class JwebSecurityInterceptor implements Interceptor {

    private JwebSecurityConfig config;

    public JwebSecurityInterceptor(JwebSecurityConfig config) {
        this.config = config;
    }

    @Override
    public void intercept(Invocation inv) {
        // 当配置开关未开启时，不执行逻辑代码
        if (config.getPerms().isEnable()) {
            // 授权控制判断处理
            AuthzProcesser ah = JwebSecurityUtils.getAuthzProcesser(inv.getActionKey());
            // 存在访问控制处理器。
            if (ah != null) {
                Controller c = inv.getController();
                try {
                    // 执行权限检查。
                    ah.assertAuthorized();
                } catch (UnauthenticatedException lae) {
                    // RequiresAuthentication，未满足时，抛出未经授权的异常。
                    // 如果没有进行身份验证，返回HTTP401状态码,或者跳转到默认登录页面
                    String loginUrl = config.getAuthc().getLoginUrl();
                    if (StrKit.notBlank(loginUrl)) {
                        try {
                            JwebUtils.redirect(inv.getController().getRequest(), inv.getController().getResponse(), loginUrl);
                        } catch (IOException e) {
                            e.printStackTrace();
                            inv.getController().renderError(401);
                        }
                    } else {
                        inv.getController().renderError(401);
                    }
                    return;
                } catch (AuthorizationException ae) {
                    // RequiresRoles，RequiresPermissions授权异常
                    // 如果没有权限访问对应的资源，返回HTTP状态码403，或者调转到为授权页面
                    String failureUrl = config.getPerms().getFailureUrl();
                    if (StrKit.notBlank(failureUrl)) {
                        c.forwardAction(failureUrl);
                    } else {
                        c.renderError(403);
                    }
                    return;
                }
            }
        }

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
