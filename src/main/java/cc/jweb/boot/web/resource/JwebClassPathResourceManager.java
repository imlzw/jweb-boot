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

package cc.jweb.boot.web.resource;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.URLResource;

import java.io.IOException;
import java.net.URL;

/**
 * jweb类路径资源管理器
 * 参考自：io.undertow.server.handlers.resource.ClassPathResourceManager
 */
public class JwebClassPathResourceManager extends ClassPathResourceManager {

    public JwebClassPathResourceManager(ClassLoader loader, Package p) {
        super(loader, p);
    }

    public JwebClassPathResourceManager(ClassLoader classLoader, String prefix) {
        super(classLoader, prefix);
    }

    public JwebClassPathResourceManager(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public Resource getResource(final String path) throws IOException {
        Resource resource = super.getResource(path);
        // 如果是jar资源，变更Resource类型
        return resource;
    }

}
