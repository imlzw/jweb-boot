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
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.setting.Setting;
//import com.ag777.util.gson.GsonUtils;
//import com.ag777.util.lang.StringUtils;
//import com.jfinal.config.*;
//import com.jfinal.kit.PathKit;
//import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
//import com.jfinal.plugin.druid.DruidPlugin;
//import com.jfinal.template.Engine;
//import com.ndasec.jfinal.enhance.annotation.Controller;
//import com.ndasec.jfinal.enhance.annotation.Model;
//import com.ndasec.jfinal.enhance.config.JFinalConfig;
//import com.ndasec.jfinal.enhance.hanlder.ParameterFilterHandler;
//import com.ndasec.jfinal.enhance.interceptor.ParameterValidatorInterceptor;
//import com.ndasec.jfinal.enhance.kit.StrKit;
//import com.ndasec.jfinal.handler.WebSocketHandler;
//import com.ndasec.jweb.web.generator.utils.GeneratorUtils;
//import com.ndasec.jweb.web.generator.utils.HumpNameUtils;
//import com.ndasec.jweb.web.generator.utils.JsonUtils;
//import com.ndasec.jweb.web.generator.utils.ModelUtils;
//import org.apache.log4j.Logger;
//import org.reflections.Reflections;
//import org.reflections.scanners.ResourcesScanner;
//import org.reflections.util.ClasspathHelper;
//import org.reflections.util.ConfigurationBuilder;
//import org.reflections.util.FilterBuilder;
//
//import java.net.URL;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * 默认jfinal框架配置项
// *
// * @author LinCH
// */
//public class DefaultConfig extends JFinalConfig {
//
//    private static Logger logger = Logger.getLogger(DefaultConfig.class);
//
//    public static Setting config = new Setting("config/jfinal.txt");
//
//
//    /**
//     * 配置常量
//     */
//    @Override
//    public void configConstant(Constants me) {
//        // TODO Auto-generated method stub
//        me.setDevMode(config.getBool("dev_mode", "common", false));
//    }
//
//    private void initEngine(Engine me) {
//        me.addSharedMethod(new StringUtils());
//        me.addSharedMethod(new HumpNameUtils());
//        me.addSharedMethod(new ModelUtils());
//        me.addSharedMethod(new GeneratorUtils());
//        me.addSharedMethod(new JsonUtils());
//        me.addSharedMethod(new com.ndasec.jweb.web.generator.utils.StrKit());
//    }
//
//    @Override
//    public void configEngine(Engine me) {
//        super.configEngine(me);
//        initEngine(me);
//    }
//
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    @Override
//    public void configPlugin(Plugins me) {
//        // TODO Auto-generated method stub
//        /**
//         * 数据库插件
//         */
//        Collection<URL> urls = ClasspathHelper.forPackage("/");
//        for (URL url : urls) {
//            System.out.println(url.toString());
//        }
//        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(urls).setScanners(new ResourcesScanner()));
//        Set<String> _sql = reflections.getResources(Pattern.compile(".*\\.stl"));
//        for (String sql : _sql) {
//            System.out.println(sql);
//        }
//        Setting setting = new Setting("config/jdbc.txt");
//        boolean isFirst = true;
//        /* 多数据源的支持，根据jdbc.txt配置文件中的setting设置方式 */
//        Map<String, DruidPlugin> dps = new HashMap<String, DruidPlugin>();
//        for (int i = 0, ln = setting.getGroups().size(); i < ln; i++) {
//            //for(String g : setting.getGroups()){
//            String g = setting.getGroups().get(i);
//            if (dps.get(g) == null) {
//                dps.put(g, new DruidPlugin(setting.getByGroup("jdbcUrl", g), setting.getByGroup("user", g), setting.getByGroup("password", g)));
//            }
//            me.add(dps.get(g));
//            ActiveRecordPlugin arp = new ActiveRecordPlugin(g, dps.get(g));
//            arp.setBaseSqlTemplatePath(PathKit.getRootClassPath());
//            arp.setDevMode(config.getBool("dev_mode", "common", false));
//            arp.setShowSql(config.getBool("dev_mode", "common", false));
//            arp.getEngine().setDevMode(arp.getDevMode());
//            arp.getEngine().setToClassPathSourceFactory().setBaseTemplatePath(null);
//            initEngine(arp.getEngine());
//
//
//            /* 获得sqltemplate文件 */
//            if (_sql != null) {
//                for (String sql : _sql) {
//                    // 过滤掉代码生成器中的模板文件
//                    if (sql.indexOf("generator/template") < 0) {
//                        arp.addSqlTemplate(sql);
//                    }
//                }
//            }
//            /* 添加模型 */
//            Reflections _reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("")).filterInputsBy(new FilterBuilder().include(".+\\.model\\.[^\\.]+\\.class")));
//            //Reflections _reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("/")).filterInputsBy(new FilterBuilder().include(".*\\.model\\.class")));
//            Set<Class<?>> models = _reflections.getTypesAnnotatedWith(Model.class);
//            if (models != null) {
//                for (Class _m : models) {
//                    Model m = (Model) _m.getAnnotation(Model.class);
//                    /* 如果模型数据源为空，就要判断当前数据源是否为第一个 */
//                    if ((g.equals(m.ds()) || (StrUtil.isBlank(m.ds()) && isFirst))) {
//                        if (StrUtil.isBlank(m.key())) {
//                            arp.addMapping(m.table(), _m);
//                        } else {
//                            arp.addMapping(m.table(), m.key(), _m);
//                        }
//                    }
//                }
//            }
//            me.add(arp);
//            isFirst = false;
//        }
//    }
//
//    @Override
//    public void configInterceptor(Interceptors me) {
//        // TODO Auto-generated method stub
//        me.add(new ParameterValidatorInterceptor());
//    }
//
//    @Override
//    public void configHandler(Handlers me) {
//        me.add(new WebSocketHandler("^/ws"));
//        me.add(new ParameterFilterHandler(config.getSetting("cn.modfun.web.core.hanlder.ParameterFilterHandler")));
//    }
//
//
//    public static void main(String[] args) {
//        // /^\.\/routes(.*)\/route\.js$/
//        Pattern pattern = Pattern.compile(".+\\.controller\\.[^\\.]+\\.class");
//        Matcher matcher = pattern.matcher("a.b.c.controller.aaa.class");
//        boolean matches = matcher.matches();// true
//        System.out.println(matches);
//
//    }
//}
