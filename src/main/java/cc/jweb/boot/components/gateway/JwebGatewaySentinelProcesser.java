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

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.LogKit;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/7
 */
public class JwebGatewaySentinelProcesser {


    public void process(Runnable runnable, JwebGatewayConfig config, HttpServletRequest req, HttpServletResponse resp) {
        Entry entry = null;
            String resourceName = JwebGatewayUtil.buildResource(config, req);
        try {
            entry = SphU.entry(resourceName, ResourceTypeConstants.COMMON_API_GATEWAY, EntryType.IN);
            runnable.run();
        } catch (BlockException ex) {
            processBlocked(config, req, resp);
        } catch (Exception ex) {
            Tracer.traceEntry(ex, entry);
            throw ex;
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

    private static void processBlocked(JwebGatewayConfig config, HttpServletRequest req, HttpServletResponse resp) {
        StringBuffer url = req.getRequestURL();

        if ("GET".equals(req.getMethod()) && StrUtil.isNotBlank(req.getQueryString())) {
            url.append("?").append(req.getQueryString());
        }

        try {
            if (StringUtil.isNotBlank(config.getSentinelBlockPage())) {
                String redirectUrl = config.getSentinelBlockPage() + "?http_referer=" + url.toString();
                resp.sendRedirect(redirectUrl);
            } else if (config.getSentinelBlockJsonMap() != null && !config.getSentinelBlockJsonMap().isEmpty()) {
                writeDefaultBlockedJson(resp, config.getSentinelBlockJsonMap());
            } else {
                writeDefaultBlockedPage(resp);
            }
        } catch (IOException ex) {
            LogKit.error(ex.toString(), ex);
        }

    }

    protected static final String contentType = "application/json; charset=utf-8";

    private static void writeDefaultBlockedJson(HttpServletResponse resp, Map map) throws IOException {
        resp.setStatus(200);
        resp.setContentType(contentType);
        PrintWriter out = resp.getWriter();
        out.print(JsonKit.toJson(map));
    }


    private static void writeDefaultBlockedPage(HttpServletResponse resp) throws IOException {
        resp.setStatus(200);
        PrintWriter out = resp.getWriter();
        out.print("Blocked by Sentinel (flow limiting) in Jboot");
    }

    public static void main(String[] args) {
    }
}
