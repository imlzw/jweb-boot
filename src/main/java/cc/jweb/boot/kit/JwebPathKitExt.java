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

package cc.jweb.boot.kit;

import io.jboot.app.PathKitExt;

import java.io.File;
import java.lang.reflect.Field;

public class JwebPathKitExt {

    /**
     * 初始化Jfinal与jboot中关于PathKitExt的rootClassPath,
     * 实现自定义classpath到webapp/WEB-INF/classes,习惯于tomcat下的webroot下的WEB-INF/classes结构
     */
    public static void initPathKitExtRootClassPath() {
        try {
            Field jfinalRootClassPath = com.jfinal.server.undertow.PathKitExt.class.getDeclaredField("rootClassPath");
            Field jbootRootClassPath = PathKitExt.class.getDeclaredField("rootClassPath");
            jbootRootClassPath.setAccessible(true);
            jfinalRootClassPath.setAccessible(true);
            String webRootPath = PathKitExt.getWebRootPath();
            String webappClassPath = webRootPath + File.separator + "WEB-INF" + File.separator+ "classes";
            try {
                jfinalRootClassPath.set(null, webappClassPath);
                System.out.println("Jweb init JFinal -> PathKitExt -> rootClassPath: " + webappClassPath);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            try {
                jbootRootClassPath.set(null, webappClassPath);
                System.out.println("Jweb init JBoot -> PathKitExt -> rootClassPath: " + webappClassPath);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
