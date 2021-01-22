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

package cc.jweb.boot.http;

import cc.jweb.boot.components.nameservice.JwebDiscoveryConfig;
import cc.jweb.boot.utils.lang.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 网络工具集
 */
public class NetUtils {
    // returned port range is [30000, 39999]
    private static final int RND_PORT_START = 30000;
    private static final int RND_PORT_RANGE = 10000;

    public static int getAvailablePort() {
        try (ServerSocket ss = new ServerSocket()) {
            ss.bind(null);
            return ss.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        }
    }

    public static int getRandomPort() {
        return RND_PORT_START + ThreadLocalRandom.current().nextInt(RND_PORT_RANGE);
    }
}
