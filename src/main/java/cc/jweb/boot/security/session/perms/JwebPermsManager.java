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

package cc.jweb.boot.security.session.perms;

import cc.jweb.boot.security.session.account.JwebSecurityAccount;

/**
 * Jweb 权限管理器
 *
 */
public interface JwebPermsManager {

    // 是否存在角色
    public abstract boolean hasRole(JwebSecurityAccount account, String role);

    // 批量判断是否存在角色
    public abstract boolean[] hasRoles(JwebSecurityAccount account, String... roles);

    // 是否存在所有角色
    public abstract boolean hasAllRoles(JwebSecurityAccount account, String... roles);

    // 是否存在权限授权
    public abstract boolean isPermitted(JwebSecurityAccount account, String permission);

    // 批量判断是否存在权限授权
    public abstract boolean[] isPermitted(JwebSecurityAccount account, String... permissions);

    // 是否存在所有权限授权
    public abstract boolean isPermittedAll(JwebSecurityAccount account, String... permissions);

    // 注销账户权限数据
    public abstract void invalidate(JwebSecurityAccount account);

}
