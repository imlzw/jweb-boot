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

package cc.jweb.boot.handler;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * layuiAdmin框架的路由处理程序
 */
public class LayuiAdminRouteHandler extends Handler {
    @Override
    public void handle(String target, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean[] isHandled) {
        // 重定向layuiAdmin的视图地址到jfinal的根地址
        String key = "assets/layuiadmin/dist/views/_";
        String layoutKey = "assets/layuiadmin/dist/views/layout.html";
        if (target.indexOf(layoutKey) >= 0) {
            target = "/layout";
        } else {
            String[] redirestKeys = {key};
            for (String redirestKey : redirestKeys) {
                int preIdx = target.indexOf(key);
                if (preIdx >= 0) {
                    int postIdx = target.indexOf(".html");
                    target = target.substring(preIdx + key.length(), postIdx);
                    break;
                }
            }
        }
        next.handle(target, httpServletRequest, httpServletResponse, isHandled);
    }
}
