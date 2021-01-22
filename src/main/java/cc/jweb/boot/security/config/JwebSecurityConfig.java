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

package cc.jweb.boot.security.config;

/**
 * jweb security config
 *
 * @author imlzw
 */
public class JwebSecurityConfig {

    // 认证配置
    private JwebSecurityAuthcConfig authc = new JwebSecurityAuthcConfig();
    // 权限配置
    private JwebSecurityPermsConfig perms = new JwebSecurityPermsConfig();
    // jwt配置
    private JwebJwtConfig jwt = new JwebJwtConfig();
    private boolean enable;
    private String sessionType = "DEFAULT";
    // session超时时长 （秒）
    private int sessionTimeout = 30 * 60;

    public JwebSecurityAuthcConfig getAuthc() {
        return authc;
    }

    public void setAuthc(JwebSecurityAuthcConfig authc) {
        if (authc != null) {
            this.authc = authc;
        }
    }

    public JwebSecurityPermsConfig getPerms() {
        return perms;
    }

    public void setPerms(JwebSecurityPermsConfig perms) {
        if (perms != null) {
            this.perms = perms;
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public JwebJwtConfig getJwt() {
        return jwt;
    }

    public void setJwt(JwebJwtConfig jwt) {
        this.jwt = jwt;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
}
