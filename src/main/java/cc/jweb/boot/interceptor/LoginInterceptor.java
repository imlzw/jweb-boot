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

package cc.jweb.boot.interceptor;


import cc.jweb.boot.common.lang.Error;
import cc.jweb.boot.common.lang.Result;
import cc.jweb.boot.utils.lang.StringUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class LoginInterceptor implements Interceptor {

    public static boolean isAjax(HttpServletRequest request) {
        if ((request.getHeader("X-Requested-With") != null || request.getHeader("x-requested-with") != null) && (request.getHeader("X-Requested-With").contains("XMLHttpRequest") || request.getHeader("x-requested-with").contains("XMLHttpRequest"))) {
            return true;
        } else {
            return StringUtils.isNotBlank(request.getParameter("_dataType")) && request.getParameter("_dataType").equals("json");
        }
    }

    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        HttpServletRequest request = controller.getRequest();
//		System.out.println("Path:"+request.getContextPath());
        /**
         * 先判断会话中是否存在用户信息
         */
        Object user = controller.getSessionAttr("user");
        if (user == null) {
            Result result = new Result(new Error("450", "用户未登录或会话过期"));
            if (isAjax(request)) {
                if (isReturnHtml(request)) {
                    controller.setAttr("result", result);
                    try {
                        controller.getResponse().getWriter().write("<script>window.location.href='/';</script>");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    controller.getResponse().setStatus(450);
                    controller.renderJson(result);
                }
            } else {
                controller.setAttr("result", result);
                controller.redirect(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/");
            }
            return;
        }
        inv.invoke();
    }

    private boolean isReturnHtml(HttpServletRequest request) {
        return (request.getHeader("Accept") != null && request.getHeader("Accept").contains("text/html")) || (request.getHeader("accept") != null && request.getHeader("accept").contains("text/html"));
    }

}
