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

package cc.jweb.boot.security.utils;

import cc.jweb.boot.security.JwebSecurityManager;
import cc.jweb.boot.security.session.JwebSecuritySession;
import cc.jweb.boot.security.session.account.JwebSecurityAccount;

/**
 * jweb 安全工具类，简化安全操作
 */
public class JwebSecurityUtils {

    /**
     * 获取session会话
     *
     * @return
     */
    public static JwebSecuritySession getSession() {
        return getSecurityManager().getSession();
    }

    /**
     * 是否已经认证，是否已经登录
     *
     * @return
     */
    public static boolean isAuthentication() {
        return getSession().isAuthentication();
    }

    /**
     * 获取账户
     *
     * @return
     */
    public static JwebSecurityAccount getAccount() {
        return getSession().getAccount();
    }

    /**
     * 设置账户
     *
     * @param account
     */
    public static void setAccount(JwebSecurityAccount account) {
        getSession().setAccount(account);
    }

    /**
     * 获取安全管理器
     *
     * @return
     */
    public static JwebSecurityManager getSecurityManager() {
        return JwebSecurityManager.me();
    }

    /**
     * 注销session会话
     */
    public static void invalidate() {
        getSession().invalidate();
    }
}
