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
package cc.jweb.boot.security.directives;

import cc.jweb.boot.security.session.account.JwebSecurityAccount;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;

/**
 * 获取当前登录的JwebSecurityAccount帐户信息
 * #jwebAccount()
 *      #(account)
 * #end
 */
@JFinalDirective("jwebAccount")
public class JwebAccountDirective extends JwebSecurityDirectiveBase {

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        if (getSession() != null && getSession().getAccount() != null) {
            JwebSecurityAccount account = getSession().getAccount();
            scope.setLocal("account", account);
            renderBody(env, scope, writer);
        }
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
