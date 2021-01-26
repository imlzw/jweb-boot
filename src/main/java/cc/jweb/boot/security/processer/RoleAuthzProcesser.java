/**
 * Copyright (c) 2011-2017, dafei 李飞 (myaniu AT gmail DOT com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.jweb.boot.security.processer;

import cc.jweb.boot.security.annotation.Logical;
import cc.jweb.boot.security.annotation.RequiresRoles;
import cc.jweb.boot.security.exception.AuthorizationException;

import java.lang.annotation.Annotation;

/**
 * 基于角色的访问控制处理器，非单例模式运行。
 *
 * @author dafei
 */
public class RoleAuthzProcesser extends AbstractAuthzProcesser {

    private final Annotation annotation;

    public RoleAuthzProcesser(Annotation annotation) {
        this.annotation = annotation;
    }

    public void assertAuthorized() throws AuthorizationException {
        //if (!(annotation instanceof RequiresRoles)) return;
        RequiresRoles rrAnnotation = (RequiresRoles) annotation;
        String[] roles = rrAnnotation.value();

        if (roles.length == 1) {
            getSession().checkRole(roles[0]);
            return;
        }
        if (Logical.AND.equals(rrAnnotation.logical())) {
            getSession().checkRoles(roles);
            return;
        }
        if (Logical.OR.equals(rrAnnotation.logical())) {
            // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
            boolean hasAtLeastOneRole = false;
            for (String role : roles) if (getSession().hasRole(role)) hasAtLeastOneRole = true;
            // Cause the exception if none of the role match, note that the exception message will be a bit misleading
            if (!hasAtLeastOneRole) getSession().checkRole(roles[0]);
        }
    }
}
