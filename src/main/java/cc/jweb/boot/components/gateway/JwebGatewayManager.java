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
package cc.jweb.boot.components.gateway;

import cc.jweb.boot.components.nameservice.JwebDiscoveryManager;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.jboot.Jboot;
import io.jboot.app.config.JbootConfigUtil;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/21
 */
public class JwebGatewayManager {

    private static JwebGatewayManager me = new JwebGatewayManager();

    public static JwebGatewayManager me() {
        return me;
    }

    private Map<String, JwebGatewayConfig> configMap;

    public void init() {
        Map<String, JwebGatewayConfig> configMap = JbootConfigUtil.getConfigModels(JwebGatewayConfig.class, "jweb.gateway");
        if (configMap != null && !configMap.isEmpty()) {

            for (Map.Entry<String, JwebGatewayConfig> e : configMap.entrySet()) {
                JwebGatewayConfig config = e.getValue();
                if (config.isConfigOk() && config.isEnable()) {
                    if (StrUtil.isBlank(config.getName())) {
                        config.setName(e.getKey());
                    }
                    registerConfig(config);
                }
            }
        }
    }


    public synchronized void registerConfig(JwebGatewayConfig config) {
        if (configMap == null) {
            configMap = new ConcurrentHashMap<>();
        }
        configMap.put(config.getName(), config);
    }


    public JwebGatewayConfig removeConfig(String name) {
        return configMap == null ? null : configMap.remove(name);
    }


    public JwebGatewayConfig getConfig(String name) {
        return configMap == null ? null : configMap.get(name);
    }


    public Map<String, JwebGatewayConfig> getConfigMap() {
        return configMap;
    }

    public JwebGatewayConfig matchingConfig(HttpServletRequest req) {
        if (configMap != null && !configMap.isEmpty()) {
            Iterator<JwebGatewayConfig> iterator = configMap.values().iterator();
            while (iterator.hasNext()) {
                JwebGatewayConfig config = iterator.next();
                if (config.matches(req)) {
                    return config;
                }
            }
        }
        return null;
    }


}
