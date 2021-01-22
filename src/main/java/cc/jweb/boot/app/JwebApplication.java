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

package cc.jweb.boot.app;

import cc.jweb.boot.app.config.JwebConfigManager;
import cc.jweb.boot.app.undertow.JwebUndertowConfig;
import cc.jweb.boot.app.undertow.JwebUndertowServer;
import cc.jweb.boot.kit.JwebPathKitExt;
import com.jfinal.server.undertow.*;
import io.jboot.app.JbootApplication;
import io.jboot.app.JbootApplicationConfig;
import io.jboot.app.JbootWebBuilderConfiger;

import javax.servlet.DispatcherType;

public class JwebApplication extends JbootApplication {

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {
        JbootApplicationConfig jbootApplicationConfig = initConfig(args);
        start(createServer(jbootApplicationConfig));
    }

    /**
     * 初始化配置
     * @param args
     */
    private static JbootApplicationConfig initConfig(String[] args) {
        // 部署模式下才初始化PathKitExt下的rootClassPath。
        if (UndertowKit.isDeployMode()) {
            // 初始化PathKitExt rootClassPath值
            JwebPathKitExt.initPathKitExtRootClassPath();
        }
        // 先重置PropExt的classLoaderKit对象为JwebClassLoaderKit
        JwebUndertowConfig.resetPropExtClassLoaderKit();
        // 初始化jboot配置管理对象
        JwebConfigManager.me().resetJbootConfigManager();
        JbootApplicationConfig appConfig = ApplicationUtil.getAppConfig(args);
        // 生成最终配置文件jweb.generator.final.properties
        JwebConfigManager.me().resetOtherConfig();
        return appConfig;
    }

    public static void run(String[] args, JbootWebBuilderConfiger configer) {
        start(createServer(args, configer));
    }

    public static void start(UndertowServer server) {
        server.start();
    }

    /**
     * 创建 Undertow 服务器，public 用于可以给第三方创建创建着急的 Server
     *
     * @return 返回 UndertowServer
     */
    public static UndertowServer createServer(JbootApplicationConfig appConfig) {
        return createServer(appConfig, createUndertowConfig(appConfig), null);
    }


    public static UndertowServer createServer(JbootApplicationConfig appConfig
            , UndertowConfig undertowConfig
            , JbootWebBuilderConfiger configer) {

        ApplicationUtil.printBannerInfo(appConfig);
        ApplicationUtil.printApplicationInfo(appConfig);
        ApplicationUtil.printClassPath();


        return new JwebUndertowServer(undertowConfig)
                .setDevMode(ApplicationUtil.isDevMode())
                .configWeb(webBuilder -> {
                    tryAddMetricsSupport(webBuilder);
                    tryAddShiroSupport(webBuilder);
                    tryAddWebSocketSupport(webBuilder);
                    if (configer != null) {
                        configer.onConfig(webBuilder);
                    }
                });
    }


    public static UndertowConfig createUndertowConfig(JbootApplicationConfig appConfig) {
        UndertowConfig undertowConfig = new JwebUndertowConfig(appConfig.getJfinalConfig());
        undertowConfig.addSystemClassPrefix("io.jboot.app");
        undertowConfig.addHotSwapClassPrefix("io.jboot");
        return undertowConfig;
    }


    private static void tryAddMetricsSupport(WebBuilder webBuilder) {
        String url = ApplicationUtil.getConfigValue("jboot.metric.url");
        String reporter = ApplicationUtil.getConfigValue("jboot.metric.reporter");
        if (url != null && reporter != null) {
            webBuilder.addServlet("MetricsAdminServlet", "com.codahale.metrics.servlets.AdminServlet")
                    .addServletMapping("MetricsAdminServlet", url.endsWith("/*") ? url : url + "/*");
            webBuilder.addListener("io.jboot.support.metric.JbootMetricServletContextListener");
            webBuilder.addListener("io.jboot.support.metric.JbootHealthCheckServletContextListener");
        }
    }


    private static void tryAddShiroSupport(WebBuilder webBuilder) {
        String iniConfig = ApplicationUtil.getConfigValue("jboot.shiro.ini");
        if (iniConfig != null) {
            String urlMapping = ApplicationUtil.getConfigValue("jboot.shiro.urlMapping");
            if (urlMapping == null) {
                urlMapping = "/*";
            }
            webBuilder.addListener("org.apache.shiro.web.env.EnvironmentLoaderListener");
            webBuilder.addFilter("shiro", "com.ndasec.jweb.plugin.shiro.JwebShiroFilter")
                    .addFilterUrlMapping("shiro", urlMapping, DispatcherType.REQUEST);

        }
    }

    private static void tryAddWebSocketSupport(WebBuilder webBuilder) {
        String websocketEndpoint = ApplicationUtil.getConfigValue("jboot.web.webSocketEndpoint");
        if (websocketEndpoint != null && websocketEndpoint.trim().length() > 0) {
            String[] classStrings = websocketEndpoint.split(",");
            for (String c : classStrings) {
                webBuilder.addWebSocketEndpoint(c.trim());
            }
        }
    }
}
