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

package cc.jweb.boot.utils.file;

import cc.jweb.boot.utils.lang.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;


/**
 * 路径工具类
 *
 * @author ag777
 * @version last modify at 2018年05月21日
 */
public class PathUtils {

    /**
     * 获取resource文件夹下资源文件的io流
     *
     * @param clazz    clazz
     * @param filePath filePath
     * @return
     */
    public static InputStream getAsStream(String filePath, Class<?> clazz) {
        if (clazz == null) {
            clazz = PathUtils.class;
        }
        return clazz.getClassLoader().getResourceAsStream(filePath);
    }

    /**
     * 获取src路径，必须传类，如果用这个类会得到lib包的路径
     * <p>
     * 返回结果如:/D:/tools/programming/eclipse_neon/Utils-Java/bin/
     * </p>
     *
     * @param clazz clazz
     * @return
     */
    public static String srcPath(Class<?> clazz) {
        if (clazz == null) {
            clazz = PathUtils.class;
        }
        java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        String filePath = url.getPath();
        try {
            filePath = java.net.URLDecoder.decode(filePath, "utf-8");
        } catch (Exception e) {
//			e.printStackTrace();
        }
        File file = new File(filePath);
        if (!file.isDirectory()) {    //可能是jar包，因为结果是/xxxx/xx.jar,所以我们要去得到jar包的同级路径
            return file.getParent();
        }
        return filePath;
    }

    /**
     * 获取src路径中的子路径
     * <p>
     * srcPath(PathUtils.class, "config/") 得到xxx/src/config/
     * </p>
     *
     * @param clazz   clazz
     * @param subPath subPath
     * @return
     */
    public static String srcPath(Class<?> clazz, String subPath) {
        return StringUtils.concatFilePath(srcPath(clazz), subPath);
    }

    /**
     * 获取相对路径
     * <p>
     * <pre>{@code
     *  PathUtils.getRelativizePath("f:\\a\\b.txt","f:\\").toString(); => "a\\b.txt"
     *  }</pre>
     *
     * @param targetPath 目标路径(需要转化为相对路径的绝对路径)
     * @param basePath   基础路径(标尺)
     * @return
     */
    public static String getRelativizePath(String targetPath, String basePath) {
        return Paths.get(basePath).relativize(Paths.get(targetPath)).toString();
    }
}