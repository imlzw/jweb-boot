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

package cc.jweb.boot.test.security.authc.controller;

import cc.jweb.boot.controller.BaseController;
import cc.jweb.boot.security.session.account.JwebSecurityAccount;
import cc.jweb.boot.security.utils.JwebSecurityUtils;
import com.jfinal.core.ActionKey;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "/authc")
public class AuthcController extends BaseController {

    @ActionKey("/login")
    public void login() {
        // 处理用户登录操作，验证成功后，设置账户信息
        String uid = getPara("uid");
        String uname = getPara("uname");
        if (uid != null) {
            JwebSecurityUtils.setAccount(new JwebSecurityAccount(uid, uname));
        }
        if (JwebSecurityUtils.isAuthentication()) {
            renderText("登录成功！" + JwebSecurityUtils.getAccount());
        } else {
            renderHtml("<a href='/login?uid=2&uname=测试用户' >登录</a>");
        }
    }


    @ActionKey("/userInfo")
    public void userInfo() {
        JwebSecurityAccount account = JwebSecurityUtils.getAccount();
        renderText("account:" + account);
    }

    @ActionKey("/logout")
    public void logout() {
        JwebSecurityUtils.invalidate();
        redirect("/login");
    }

}
