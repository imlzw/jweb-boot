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

package cc.jweb.boot.config;

import cc.jweb.boot.db.JwebArpManager;
import cc.jweb.boot.security.JwebSecurityManager;
import cc.jweb.boot.web.handler.JwebActionHandler;
import cc.jweb.boot.web.render.JwebBootRenderFactory;
import cc.jweb.boot.web.resource.EngineResourceFactory;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.core.listener.JbootAppListenerBase;

/**
 * jweb-boot重写配置
 */
public class JwebBootAppConfig extends JbootAppListenerBase {

    /**
     * 供Shiro插件使用。
     */
    Routes routes;

    @Override
    public void onRouteConfig(Routes me) {
        this.routes = me;

    }


    @Override
    public void onInterceptorConfig(Interceptors me) {
        me.add(JwebSecurityManager.me().getInterceptor());
    }

    @Override
    public void onEngineConfig(Engine engine) {
        engine.setSourceFactory(new EngineResourceFactory());
    }

    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {
        //
        handlers.add(JwebSecurityManager.me().getSecurityHandler());
        handlers.setActionHandler(new JwebActionHandler());
    }

    @Override
    public void onConstantConfig(Constants constants) {
        super.onConstantConfig(constants);
        constants.setRenderFactory(JwebBootRenderFactory.getInstance());
    }

    @Override
    public void onPluginConfig(JfinalPlugins plugins) {
        super.onPluginConfig(plugins);
        JwebArpManager.initArpPlugins(plugins);
    }


}
