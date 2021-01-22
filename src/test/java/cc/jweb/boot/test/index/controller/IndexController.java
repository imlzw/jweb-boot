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

package cc.jweb.boot.test.index.controller;

import cc.jweb.boot.controller.BaseController;
import cc.jweb.boot.security.session.account.JwebSecurityAccount;
import cc.jweb.boot.security.utils.JwebSecurityUtils;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping(value = "/")
public class IndexController extends BaseController {
    public void index() {
        renderText("OK,Account:" + JwebSecurityUtils.getAccount());
    }
}
