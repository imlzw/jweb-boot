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

package cc.jweb.boot.web.handler;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import io.jboot.web.handler.JbootActionHandler;

public class JwebActionHandler extends JbootActionHandler {
    /**
     * 方便子类复写、从而可以实现 自定义 Invocation 的功能
     *
     * @param action
     * @param controller
     * @return
     */
    public Invocation getInvocation(Action action, Controller controller) {
        return new JwebActionInvocation(action, controller);
    }


}
