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

package cc.jweb.boot.config;//package com.ndasec.jfinal.config;
//
//import cn.hutool.setting.Setting;
//import com.alibaba.fastjson.JSON;
//import com.jfinal.config.*;
//import com.jfinal.core.JFinal;
//import com.jfinal.ext.interceptor.SessionInViewInterceptor;
//import com.jfinal.handler.Handler;
//import com.jfinal.render.RenderManager;
//import com.jfinal.template.Engine;
//import com.ndasec.jfinal.elasticsearch.ESPlugin;
//import com.ndasec.jfinal.handler.LayuiAdminRouteHandler;
//import com.ndasec.jfinal.interceptor.ExceptionInterceptor;
//import com.ndasec.jfinal.interceptor.LoginInterceptor;
//import com.ndasec.jfinal.plugin.shiro.ShiroInterceptor;
//import com.ndasec.jfinal.plugin.shiro.ShiroKit;
//import com.ndasec.jfinal.plugin.shiro.ShiroPlugin;
//import com.ndasec.jweb.web.support.file.OfficeTemplatePlugin;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.web.filter.mgt.DefaultFilter;
//import org.eclipse.jetty.http.HttpStatus;
//import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.webapp.WebAppContext;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Properties;
//
//import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_CONNECTIONPROPERTIES;
//import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_URL;
//
//public class JFinalConfig extends DefaultConfig {
//    /**
//     * 供Shiro插件使用。
//     */
//    Routes routes;
//
//    @Override
//    public void configRoute(Routes me) {
//        super.configRoute(me);
//        this.routes = me;
//    }
//
//    @Override
//    public void configEngine(Engine me) {
//        super.configEngine(me);
//    }
//
//    @Override
//    public void configHandler(Handlers me) {
//        super.configHandler(me);
//        me.add(new LayuiAdminRouteHandler());
//    }
//
//    @Override
//    public void configConstant(Constants me) {
//        super.configConstant(me);
////        me.setErrorView(401, "/au/login.html");
////        me.setErrorView(403, "/au/login.html");
////        me.setError404View("/404.html");
////        me.setError500View("/500.html");
//    }
//
//    @Override
//    public void configInterceptor(Interceptors me) {
//
//        me.add(new ShiroInterceptor());
////        me.add(new LoginInterceptor());
//        me.add(new ExceptionInterceptor());
//        me.add(new SessionInViewInterceptor());
//    }
//
//
//    @Override
//    public void configPlugin(Plugins me) {
//        super.configPlugin(me);
//        me.add(new ESPlugin(new Setting("config/elasticsearch.txt")));
//        me.add(new OfficeTemplatePlugin(new Setting("config/elasticsearch.txt")));
//
//        //加载Shiro插件
//        //me.add(new ShiroPlugin(routes));
//        ShiroPlugin shiroPlugin = new ShiroPlugin(this.routes);
//        shiroPlugin.setLoginUrl("/");
//        shiroPlugin.setSuccessUrl("/");
//        shiroPlugin.setUnauthorizedUrl("/");
//        me.add(shiroPlugin);
//
//        /* 新增ESSql插件支持 */
//        Setting es_sql = new Setting("config/elasticsearch-sql.txt");
//        Properties properties = new Properties();
//        properties.put(PROP_URL, es_sql.getStr("jdbc.url"));
//        properties.put(PROP_CONNECTIONPROPERTIES, "client.transport.ignore_cluster_name=true");
//    }
//
//    @Override
//    public void afterJFinalStart() {
//        super.afterJFinalStart();
//        RenderManager.me().getEngine().addSharedObject("servletContext", JFinal.me().getServletContext());
//
//        System.out.println(JSON.toJSONString(JFinalConfig.config.getGroups()));
//    }
//
//
//    public static void main(String[] args) {
//        try {
//            startWithJetty(8080, "C:\\DATA\\SituationAware\\jweb-test\\target\\jweb-test-1.0", null);
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//    }
//
//
//    public static void startWithJetty(int port, String webroot, String extClassPath) throws Exception {
//        Server server = new Server(port);
//        WebAppContext context = new WebAppContext();
//        context.setContextPath("/");
//        context.setResourceBase(webroot);// 指定webapp目录
//        context.setExtraClasspath(extClassPath);
//        server.setHandler(context);
//        server.start();
//        server.join();
//    }
//
//    public static class ExampleServlet extends HttpServlet {
//
//        @Override
//        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//                throws ServletException, IOException {
//
//            resp.setStatus(HttpStatus.OK_200);
//            resp.getWriter().println("EmbeddedJetty");
//        }
//    }
//}
