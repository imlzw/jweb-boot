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

package cc.jweb.boot.security.session.impl;

import cc.jweb.boot.security.config.JwebSecurityConfig;
import cc.jweb.boot.security.session.JwebSecuritySession;
import cc.jweb.boot.security.session.account.JwebSecurityAccount;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * http Session 原生http session适配
 */
public class JwebHttpSession extends JwebSecuritySession {

    private final static String ACCOUNT_KEY = "JWEB_ACCOUNT_SESSION_KEY";

    public JwebHttpSession(HttpServletRequest request, HttpServletResponse response, JwebSecurityConfig config) {
        super(request, response, config);
        setTimeoutSeconds(getTimeoutSeconds());
    }

    @Override
    public JwebSecurityAccount getAccount() {
        HttpSession session = getRequest().getSession(false);
        return (JwebSecurityAccount) (session != null ? session.getAttribute(ACCOUNT_KEY) : null);
    }

    @Override
    public void setAccount(JwebSecurityAccount jwebSecurityAccount) {
        getRequest().getSession(true).setAttribute(ACCOUNT_KEY, jwebSecurityAccount);
    }

    @Override
    public Object getAttribute(String attrName) {
        HttpSession session = getRequest().getSession(false);
        return session != null ? session.getAttribute(attrName) : null;
    }

    @Override
    public void setAttribute(String attrName, Object value) {
        if (attrName != null) {
            getRequest().getSession(true).setAttribute(attrName, value);
        }
    }

    @Override
    public void removeAttribute(String attrName) {
        getRequest().getSession().removeAttribute(attrName);
    }

    @Override
    public void setTimeoutSeconds(int timeoutSeconds) {
        getRequest().getSession(true).setMaxInactiveInterval(timeoutSeconds);
    }

    @Override
    public boolean isAuthentication() {
        return getAccount() != null;
    }

    @Override
    public boolean hasRole(String role) {
        return false;
    }

    @Override
    public boolean[] hasRoles(String... roles) {
        return new boolean[0];
    }

    @Override
    public boolean hasAllRoles(String... roles) {
        return false;
    }

    @Override
    public boolean isPermitted(String permission) {
        return false;
    }

    @Override
    public boolean[] isPermitted(String... permissions) {
        return new boolean[0];
    }

    @Override
    public boolean isPermittedAll(String... permissions) {
        return false;
    }

    @Override
    public void invalidate() {
        HttpSession session = getRequest().getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @Override
    public void postIntercept() {
        // doing nothing
    }
}
