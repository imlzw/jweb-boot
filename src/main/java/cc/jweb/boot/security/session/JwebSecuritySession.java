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

package cc.jweb.boot.security.session;

import cc.jweb.boot.security.config.JwebSecurityConfig;
import cc.jweb.boot.security.exception.AuthorizationException;
import cc.jweb.boot.security.exception.UnauthorizedException;
import cc.jweb.boot.security.session.account.JwebSecurityAccount;
import cc.jweb.boot.security.session.perms.JwebNonePermsManager;
import cc.jweb.boot.security.session.perms.JwebPermsManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 安全框架session
 * <p>
 * 实现会话角色与权限的检查
 * 实现会话认证的检查
 *
 * @author imlzw
 */
public abstract class JwebSecuritySession {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private JwebSecurityConfig jwebSecurityConfig;
    private int timeoutSeconds = 30 * 60;
    private JwebPermsManager jwebPermsManager = new JwebNonePermsManager();

    public JwebSecuritySession(HttpServletRequest request, HttpServletResponse response, JwebSecurityConfig jwebSecurityConfig) {
        this.request = request;
        this.response = response;
        this.jwebSecurityConfig = jwebSecurityConfig;
        this.timeoutSeconds = jwebSecurityConfig.getSessionTimeout();
    }

    public JwebPermsManager getJwebPermsManager() {
        return jwebPermsManager;
    }

    public void setJwebPermsManager(JwebPermsManager jwebPermsManager) {
        this.jwebPermsManager = jwebPermsManager;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public JwebSecurityConfig getJwebSecurityConfig() {
        return jwebSecurityConfig;
    }

    public void setJwebSecurityConfig(JwebSecurityConfig jwebSecurityConfig) {
        this.jwebSecurityConfig = jwebSecurityConfig;
    }

    // 获取会话账户信息
    public abstract JwebSecurityAccount getAccount();

    // 设置会话账户信息
    public abstract void setAccount(JwebSecurityAccount jwebSecurityAccount);

    // 是否已认证
    public abstract boolean isAuthenticated();

    // 设置session属性值
    public abstract Object getAttribute(String attrName);

    // 获取session属性值
    public abstract void setAttribute(String attrName, Object value);

    // 移除session属性值
    public abstract void removeAttribute(String attrName);

    // 获取session超时时长，秒
    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    // 设置session超时时长，秒
    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    // 是否存在角色
    public boolean hasRole(String role) {
        return jwebPermsManager.hasRole(getAccount(), role);
    }

    // 批量判断是否存在角色
    public boolean[] hasRoles(String... roles) {
        return jwebPermsManager.hasRoles(getAccount(), roles);
    }

    ;

    // 是否存在所有角色
    public boolean hasAllRoles(String... roles) {
        return jwebPermsManager.hasAllRoles(getAccount(), roles);
    }

    // 是否存在权限授权
    public boolean isPermitted(String permission) {
        return jwebPermsManager.isPermitted(getAccount(), permission);
    }

    // 批量判断是否存在权限授权
    public boolean[] isPermitted(String... permissions) {
        return jwebPermsManager.isPermitted(getAccount(), permissions);
    }

    // 是否存在所有权限授权
    public boolean isPermittedAll(String... permissions) {
        return jwebPermsManager.isPermittedAll(getAccount(), permissions);
    }

    // 检查是否存在角色，不存在时，抛出异常
    public void checkRole(String role) throws AuthorizationException {
        if (!hasRole(role)) {
            throw new UnauthorizedException("Session does not have role [" + role + "]");
        }
    }

    // 检查是否存在所有角色，不存在时，抛出异常
    public void checkRoles(String... roles) {
        if (roles != null) {
            for (String role : roles) {
                checkRole(role);
            }
        }
    }

    // 检查是否有权限授权，不存在时，抛出异常
    public void checkPermission(String permission) {
        if (!isPermitted(permission)) {
            throw new UnauthorizedException("Session does not have permission [" + permission + "]");
        }
    }

    // 检查是否有所有权限授权，不存在时，抛出异常
    public void checkPermissions(String... permissions) {
        if (permissions != null && permissions.length > 0) {
            for (String perm : permissions) {
                checkPermission(perm);
            }
        }
    }

    // 注销
    public abstract void invalidate();

    // jweb security interceptor 中断执行后置回调
    public abstract void postIntercept();

    // jweb security handler 处理后置回调
    public abstract void postHandle();

}
