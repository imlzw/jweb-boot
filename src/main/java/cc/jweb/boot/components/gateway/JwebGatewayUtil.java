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

import cc.jweb.boot.components.nameservice.JwebDiscoveryManager;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/3/22
 */
public class JwebGatewayUtil {

    private static final String PATH_SPLIT = "/";
    private final static String serviceNameRegex = "\\{(.*?)\\}";
    private final static Pattern regex = Pattern.compile(serviceNameRegex);

    public static String buildProxyUrl(JwebGatewayConfig config, HttpServletRequest request) throws NacosException {
        String targetUrl = config.buildLoadBalanceStrategy().getUrl(config, request);
        StringBuilder url = new StringBuilder(parseProxyUrl(targetUrl));
        String requestURI = request.getRequestURI();
        if (StrUtil.isNotBlank(requestURI)) {
            String[] pathRewrite = config.getPathRewrite();
            if (pathRewrite != null && pathRewrite.length > 1) {
                url.append(requestURI.replaceAll(pathRewrite[0], pathRewrite[1]));
            } else {
                url.append(requestURI);
            }
        }
        if (StrUtil.isNotBlank(request.getQueryString())) {
            url.append("?").append(request.getQueryString());
        }
        return url.toString();
    }

    /**
     * 解析代理目标url
     * 将url中的服务名称，替换为真正服务发现健康实例ip
     *
     * @param proxyUrl
     * @return
     */
    public static String parseProxyUrl(String proxyUrl) throws NacosException {
        Matcher matcher = regex.matcher(proxyUrl);
        if (matcher.find()) {
            String serviceName = matcher.group(1);
            Instance instance = JwebDiscoveryManager.me().getNamingService().selectOneHealthyInstance(serviceName, true);
            if (instance == null) {
                throw new NacosException(400, "找不到");
            } else {
                proxyUrl = proxyUrl.replaceAll(serviceNameRegex, instance.getIp() + ":" + instance.getPort());
            }
        }
        return proxyUrl;
    }


    public static String buildResource(HttpServletRequest request) {
        String pathInfo = getResourcePath(request);
        if (!pathInfo.startsWith(PATH_SPLIT)) {
            pathInfo = PATH_SPLIT + pathInfo;
        }

        if (PATH_SPLIT.equals(pathInfo)) {
            return pathInfo;
        }

        // Note: pathInfo should be converted to camelCase style.
        int lastSlashIndex = pathInfo.lastIndexOf("/");

        if (lastSlashIndex >= 0) {
            pathInfo = pathInfo.substring(0, lastSlashIndex) + "/"
                    + StringUtil.trim(pathInfo.substring(lastSlashIndex + 1));
        } else {
            pathInfo = PATH_SPLIT + StringUtil.trim(pathInfo);
        }

        return pathInfo;
    }


    private static String getResourcePath(HttpServletRequest request) {
        String pathInfo = normalizeAbsolutePath(request.getPathInfo(), false);
        String servletPath = normalizeAbsolutePath(request.getServletPath(), pathInfo.length() != 0);

        return servletPath + pathInfo;
    }

    private static String normalizeAbsolutePath(String path, boolean removeTrailingSlash) throws IllegalStateException {
        return normalizePath(path, true, false, removeTrailingSlash);
    }

    private static String normalizePath(String path, boolean forceAbsolute, boolean forceRelative,
                                        boolean removeTrailingSlash) throws IllegalStateException {
        char[] pathChars = StringUtil.trimToEmpty(path).toCharArray();
        int length = pathChars.length;

        // Check path and slash.
        boolean startsWithSlash = false;
        boolean endsWithSlash = false;

        if (length > 0) {
            char firstChar = pathChars[0];
            char lastChar = pathChars[length - 1];

            startsWithSlash = firstChar == PATH_SPLIT.charAt(0) || firstChar == '\\';
            endsWithSlash = lastChar == PATH_SPLIT.charAt(0) || lastChar == '\\';
        }

        StringBuilder buf = new StringBuilder(length);
        boolean isAbsolutePath = forceAbsolute || !forceRelative && startsWithSlash;
        int index = startsWithSlash ? 0 : -1;
        int level = 0;

        if (isAbsolutePath) {
            buf.append(PATH_SPLIT);
        }

        while (index < length) {
            index = indexOfSlash(pathChars, index + 1, false);

            if (index == length) {
                break;
            }

            int nextSlashIndex = indexOfSlash(pathChars, index, true);

            String element = new String(pathChars, index, nextSlashIndex - index);
            index = nextSlashIndex;

            // Ignore "."
            if (".".equals(element)) {
                continue;
            }

            // Backtrack ".."
            if ("..".equals(element)) {
                if (level == 0) {
                    if (isAbsolutePath) {
                        throw new IllegalStateException(path);
                    } else {
                        buf.append("..").append(PATH_SPLIT);
                    }
                } else {
                    buf.setLength(pathChars[--level]);
                }

                continue;
            }

            pathChars[level++] = (char) buf.length();
            buf.append(element).append(PATH_SPLIT);
        }

        // remove the last "/"
        if (buf.length() > 0) {
            if (!endsWithSlash || removeTrailingSlash) {
                buf.setLength(buf.length() - 1);
            }
        }

        return buf.toString();
    }

    private static int indexOfSlash(char[] chars, int beginIndex, boolean slash) {
        int i = beginIndex;

        for (; i < chars.length; i++) {
            char ch = chars[i];

            if (slash) {
                if (ch == PATH_SPLIT.charAt(0) || ch == '\\') {
                    break; // if a slash
                }
            } else {
                if (ch != PATH_SPLIT.charAt(0) && ch != '\\') {
                    break; // if not a slash
                }
            }
        }

        return i;
    }
}
