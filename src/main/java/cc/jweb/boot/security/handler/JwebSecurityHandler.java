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

package cc.jweb.boot.security.handler;

import cc.jweb.boot.security.JwebSecurityManager;
import cc.jweb.boot.security.config.JwebSecurityConfig;
import cc.jweb.boot.security.session.JwebSecuritySession;
import cc.jweb.boot.security.utils.JwebUtils;
import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * jweb security handler
 * <p>
 * handle authentication and permission
 * <p>
 * 1.根据配置，过滤需要认证url
 * 2.如果是jwt认证，提取jwt信息到jwtSession中(注意：jwtSession大小有限制,cookie模式时最大4KB)
 * 3.如果是http Session认证，初始化httpSession对象
 */
public class JwebSecurityHandler extends Handler {

    private JwebSecurityConfig securityConfig;

    public JwebSecurityHandler(JwebSecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        JwebSecuritySession session = null;
        if (securityConfig.isEnable()) {
            try {
                boolean isAuthcEnable = securityConfig.getAuthc().isEnable();
                boolean isPermsEnable = securityConfig.getPerms().isEnable();
                if (isAuthcEnable || isPermsEnable) {
                    // 加载session
                    session = JwebSecurityManager.me().initSession(request, response, securityConfig);
                }
                if (isAuthcEnable) {
                    boolean isLoginUrl = target.equals(securityConfig.getAuthc().getLoginUrl());
                    if (!isLoginUrl) {
                        // 未登录
                        if (!JwebSecurityManager.me().getSession().isAuthenticated()) {
                            // 清除优先
                            boolean isClear = JwebSecurityManager.me().isActionClearSecurity(target);
                            // 注解匹配（优先），路径匹配
                            boolean isMatch = !isClear && (JwebSecurityManager.me().isActionRequiredAuthc(target) || securityConfig.getAuthc().pathMatch(target));
                            if (isMatch) {
                                JwebUtils.redirect(request, response, securityConfig.getAuthc().getLoginUrl());
                                isHandled[0] = true;
                                return;
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                isHandled[0] = true;
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                isHandled[0] = true;
                e.printStackTrace();
            } catch (InstantiationException e) {
                isHandled[0] = true;
                e.printStackTrace();
            } catch (IOException e) {
                isHandled[0] = true;
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                isHandled[0] = true;
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                isHandled[0] = true;
                e.printStackTrace();
            } catch (Exception e) {
                isHandled[0] = true;
                e.printStackTrace();
            }
        }
        if (!isHandled[0]) {
            try {
                next.handle(target, request, response, isHandled);
            } finally {
                if (session != null) {
                    session.postHandle();
                }
            }
        }
    }
}
