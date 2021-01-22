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
package cc.jweb.boot.components.gateway;

import com.jfinal.handler.Handler;
import io.jboot.components.gateway.GatewayInvocation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/22
 */
public class JwebGatewayHandler extends Handler {


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        JwebGatewayConfig config = JwebGatewayManager.me().matchingConfig(request);
        if (config != null) {
            new JwebGatewayInvocation(config,request,response).invoke();
            isHandled[0] = true;
        } else {
            next.handle(target, request, response, isHandled);
        }
    }
}
