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

package cc.jweb.boot.security.config;

/**
 * jweb安全认证配置
 */
public class JwebSecurityAuthcConfig {
    private boolean enable = false;
    private String loginUrl = "/login";
    private String[] pathFilter;
    private String[] excludeFilter;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String[] getPathFilter() {
        return pathFilter;
    }

    public void setPathFilter(String[] pathFilter) {
        this.pathFilter = pathFilter;
    }

    public String[] getExcludeFilter() {
        return excludeFilter;
    }

    public void setExcludeFilter(String[] excludeFilter) {
        this.excludeFilter = excludeFilter;
    }

    /**
     * 路径匹配
     *
     * @param path
     * @return
     */
    public boolean pathMatch(String path) {
        return true;
    }
}
