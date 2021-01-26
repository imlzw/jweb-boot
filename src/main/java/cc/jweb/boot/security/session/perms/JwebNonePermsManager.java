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
 * 空权限管理器
 * <p>
 * 所有权限返回true
 */
public class JwebNonePermsManager implements JwebPermsManager {

    @Override
    public boolean hasRole(JwebSecurityAccount account, String role) {
        return true;
    }

    @Override
    public boolean[] hasRoles(JwebSecurityAccount account, String... roles) {
        boolean[] booleans = new boolean[roles.length];
        for (int i = 0; i < roles.length; i++) {
            booleans[i] = true;
        }
        return booleans;
    }

    @Override
    public boolean hasAllRoles(JwebSecurityAccount account, String... roles) {
        return true;
    }

    @Override
    public boolean isPermitted(JwebSecurityAccount account, String permission) {
        return true;
    }

    @Override
    public boolean[] isPermitted(JwebSecurityAccount account, String... permissions) {
        boolean[] booleans = new boolean[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            booleans[i] = true;
        }
        return booleans;
    }

    @Override
    public boolean isPermittedAll(JwebSecurityAccount account, String... permissions) {
        return true;
    }

    @Override
    public void invalidate(JwebSecurityAccount account) {

    }
}
