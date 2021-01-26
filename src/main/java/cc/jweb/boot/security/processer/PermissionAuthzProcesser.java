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
import cc.jweb.boot.security.annotation.RequiresPermissions;
import cc.jweb.boot.security.exception.AuthorizationException;

import java.lang.annotation.Annotation;

/**
 * 基于权限的访问控制处理器，非单例模式运行。
 * @author dafei
 *
 */
public class PermissionAuthzProcesser extends AbstractAuthzProcesser {
    private final Annotation annotation;

    public PermissionAuthzProcesser(Annotation annotation) {
        this.annotation = annotation;
    }

    public void assertAuthorized() throws AuthorizationException {
        if (!(annotation instanceof RequiresPermissions))
            return;

        RequiresPermissions rpAnnotation = (RequiresPermissions) annotation;
        String[] perms = rpAnnotation.value();

        if (perms.length == 1) {
            getSession().checkPermission(perms[0]);
            return;
        }
        if (Logical.AND.equals(rpAnnotation.logical())) {
			getSession().checkPermissions(perms);
            return;
        }
        if (Logical.OR.equals(rpAnnotation.logical())) {
            // Avoid processing exceptions unnecessarily - "delay" throwing the
            // exception by calling hasRole first
            boolean hasAtLeastOnePermission = false;
            for (String permission : perms)
                if (getSession().isPermitted(permission))
                    hasAtLeastOnePermission = true;
            // Cause the exception if none of the role match, note that the
            // exception message will be a bit misleading
            if (!hasAtLeastOnePermission)
				getSession().checkPermission(perms[0]);

        }

    }

}
