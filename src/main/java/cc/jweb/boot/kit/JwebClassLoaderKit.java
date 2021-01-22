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

package cc.jweb.boot.kit;

import cc.jweb.boot.app.classloader.JwebHotSwapClassLoader;
import com.jfinal.server.undertow.UndertowKit;
import com.jfinal.server.undertow.hotswap.ClassLoaderKit;
import com.jfinal.server.undertow.hotswap.HotSwapResolver;
import io.jboot.app.PathKitExt;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Jweb类加载器工具
 *
 * 实现在fatjar下，使APPBASE/webapp/WEB-INF/classes成为web工程的启动HotSwapClassLoader对象的classpath之首
 */
public class JwebClassLoaderKit extends ClassLoaderKit {

    public JwebClassLoaderKit(ClassLoader parentClassLoader, HotSwapResolver hotSwapResolver) {
        super(parentClassLoader, hotSwapResolver);
        // 在部署情况下场景下重置数据。
        if (UndertowKit.isDeployMode()) {
            // 重写 classpaths，添加fatjar下的webapp/classes为首要类路径。
            String webRootPath = PathKitExt.getWebRootPath();
            List<URL> urlList = new ArrayList<>();
            String webappClassPath = webRootPath + File.separator + "WEB-INF" + File.separator + "classes";
            try {
                urlList.add(new File(webappClassPath).toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            for (URL classPathUrl : classPathUrls) {
                urlList.add(classPathUrl);
            }
            classPathUrls = urlList.toArray(new URL[urlList.size()]);
            currentClassLoader = new JwebHotSwapClassLoader(classPathUrls, parentClassLoader, hotSwapResolver);
        }
    }

}
