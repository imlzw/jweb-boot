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

package cc.jweb.boot.db;

import cc.jweb.boot.utils.lang.StringUtils;
import cc.jweb.boot.web.render.JwebBootRenderFactory;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalPlugins;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.net.URL;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 管理数据库插件
 */
public class JwebArpManager {

    /**
     * 扫描sql模板资源
     */
    public static Set<String> scanSqlTemplateResource() {
        /**
         * 数据库插件
         */
        System.out.print("Jweb Scanning SqlTemplate Resouce: ");
        Collection<URL> urls = ClasspathHelper.forPackage("");
        ResourcesScanner resourcesScanner = new ResourcesScanner();
        Pattern compile = Pattern.compile(".*\\.stl");
        resourcesScanner.setResultFilter(s -> s.endsWith(".stl"));
        ConfigurationBuilder configuration = new ConfigurationBuilder().setUrls(urls).setScanners(resourcesScanner);
        Reflections reflections = new Reflections(configuration);
        Set<String> _sql = reflections.getResources(compile);
        System.out.println(_sql);
        return _sql;
    }

    /**
     * 初始化数据库插件
     * 1.初始化插件引擎共享方法
     * 2.初始化插件sql模板文件
     *
     * @param plugins
     */
    public static void initArpPlugins(JfinalPlugins plugins) {
        Set<String> sqlTpls = scanSqlTemplateResource();
        for (IPlugin iPlugin : plugins.getPlugins().getPluginList()) {
            if (iPlugin instanceof ActiveRecordPlugin) {
                ActiveRecordPlugin activeRecordPlugin = (ActiveRecordPlugin) iPlugin;
                initArpEngine(activeRecordPlugin.getEngine());
                initArpSqlTemplate(sqlTpls, activeRecordPlugin);
            }
        }
    }

    private static void initArpSqlTemplate(Set<String> sqlTpls, ActiveRecordPlugin activeRecordPlugin) {
        String configName = activeRecordPlugin.getConfig().getName();
        for (String sqlTpl : sqlTpls) {
            // 未指定数据源的模板，添加到默认数据源插件中去
            if (!sqlTpl.startsWith("ds_")) {
                if (StringUtils.isEmpty(configName) || DbKit.MAIN_CONFIG_NAME.equalsIgnoreCase(configName)) {
                    activeRecordPlugin.addSqlTemplate(sqlTpl);
                }
            }
            // ds_{configName}.xxx.xxx.stl 配置了数据源名的模板，放在指定插件中。
            else if (sqlTpl.startsWith("ds_" + configName)) {
                activeRecordPlugin.addSqlTemplate(sqlTpl);
            }
        }
    }

    /**
     * 初始化数据库插件模板引擎
     *
     * @param engine
     */
    public static void initArpEngine(Engine engine) {
        JwebBootRenderFactory.getInstance().initEngine(engine);
    }
}
