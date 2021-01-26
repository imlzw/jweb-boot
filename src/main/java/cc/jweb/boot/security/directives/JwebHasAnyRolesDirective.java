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

import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.ArrayUtil;
import io.jboot.web.directive.annotation.JFinalDirective;


/**
 * 拥有任何一个角色
 * #jwebHasAnyRoles(roleName1,roleName2,...)
 *      body
 * #end
 */
@JFinalDirective("jwebHasAnyRoles")
public class JwebHasAnyRolesDirective extends JwebSecurityDirectiveBase {

    @Override
    public void setExprList(ExprList exprList) {
        if (exprList.getExprArray().length == 0) {
            throw new IllegalArgumentException("#jwebHasAnyRoles argument must not be empty");
        }
        super.setExprList(exprList);
    }

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        if (getSession() != null && ArrayUtil.isNotEmpty(exprList.getExprArray())) {
            for (Expr expr : exprList.getExprArray()) {
                if (getSession().hasRole(expr.eval(scope).toString())) {
                    renderBody(env, scope, writer);
                    break;
                }
            }
        }
    }

    @Override
    public boolean hasEnd() {
        return true;
    }


}