/**
 * Copyright (c) 2015-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.jweb.boot.components.gateway;

import com.alibaba.nacos.api.exception.NacosException;
import io.jboot.components.gateway.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/24
 */
public class JwebGatewayInvocation {

    private JwebGatewayConfig config;
    private JwebGatewayInterceptor[] inters;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private JwebGatewayHttpProxy proxy;

    private int index = 0;


    public JwebGatewayInvocation(JwebGatewayConfig config, HttpServletRequest request, HttpServletResponse response) {
        this.config = config;
        this.request = request;
        this.response = response;
        this.proxy = new JwebGatewayHttpProxy(config);
        this.inters = config.buildInterceptors();
    }

    public void invoke() {
        if (inters == null || inters.length == 0) {
            doInvoke();
            return;
        }
        if (index < inters.length) {
            inters[index++].intercept(this);
        } else if (index++ >= inters.length) {
            doInvoke();
        }
    }

    protected void doInvoke() {
        Runnable runnable = () -> {
            try {
                proxy.sendRequest(JwebGatewayUtil.buildProxyUrl(config, request), request, response);
            } catch (NacosException e) {
                e.printStackTrace();
            }
        };
        if (config.isSentinelEnable()) {
            new JwebGatewaySentinelProcesser().process(runnable, config, request, response);
        } else {
            runnable.run();
        }
    }


    public JwebGatewayConfig getConfig() {
        return config;
    }

    public void setConfig(JwebGatewayConfig config) {
        this.config = config;
    }

    public JwebGatewayInterceptor[] getInters() {
        return inters;
    }

    public void setInters(JwebGatewayInterceptor[] inters) {
        this.inters = inters;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public JwebGatewayHttpProxy getProxy() {
        return proxy;
    }

    public boolean hasException() {
        return proxy.getException() != null;
    }
}
