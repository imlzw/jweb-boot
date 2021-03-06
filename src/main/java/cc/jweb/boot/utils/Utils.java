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

package cc.jweb.boot.utils;

import cc.jweb.boot.utils.file.FileUtils;
import cc.jweb.boot.utils.file.PropertyUtils;
import cc.jweb.boot.utils.gson.GsonUtils;
import cc.jweb.boot.utils.lang.Console;
import cc.jweb.boot.utils.lang.interf.JsonUtilsInterf;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 工具包用的通用方法类
 * <p>
 * 所有定制操作最好在程序初始化时执行以确保程序能照预想的运行
 * </p>
 *
 * @author ag777
 * @version create on 2017年06月06日,last modify at 2018年04月24日
 */
public class Utils {

    private static JsonUtilsInterf jsonUtil = GsonUtils.get();        //懒加载

    private Utils() {
    }

    /**
     * 获取这个工具包的包名
     *
     * @return 包名
     */
    public static String getUtilsPackageName() {
        return getPageName(Utils.class);
    }

    //--配置

    /**
     * 切换开发模式,非开发模式下，部分输出不会打印出来
     *
     * @param isDeviceMode 是否是开发模式
     */
    public static void deviceMode(Boolean isDeviceMode) {
        Console.setDevMode(isDeviceMode);
    }

    /**
     * 定制文件读写编码
     *
     * @param charset 字符编码
     */
    public static void FileEncoding(Charset charset) {
        FileUtils.encodingRead(charset);
        FileUtils.encodingWrite(charset);
    }

    public static JsonUtilsInterf jsonUtils() {
        return jsonUtil;
    }

    /**
     * 定制json转换工具,工具包内部的所有json转换都会变成传入的这个
     *
     * @param JsonUtils JsonUtils
     */
    public static void jsonUtils(JsonUtilsInterf JsonUtils) {
        Utils.jsonUtil = JsonUtils;
    }

    public static String info() {
        Map<String, Object> infoMap = infoMap();
        if (infoMap != null) {
            return jsonUtils().toJson(infoMap);
        }
        return null;
    }

    public static Map<String, Object> infoMap() {
        PropertyUtils pu = new PropertyUtils();
        try {

            pu.load(Utils.class.getResourceAsStream("/utils.properties"));
            Map<String, Object> infoMap = new HashMap<String, Object>();
            infoMap.put("version", pu.get("versionName"));
            infoMap.put("last_release_date", pu.get("last_release_date"));
            return infoMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*=============内部方法==================*/

    /**
     * 获取类对应包名
     *
     * @param clazz clazz
     * @return
     */
    private static String getPageName(Class<?> clazz) {
        Package p = clazz.getPackage();
        if (p == null) {
            return "";
        } else {
            return p.getName();
        }
    }

    /**
     * 获取父包路径
     *
     * @param clazz clazz
     * @return String
     */
    public static String getParentPackageName(Class<?> clazz) {
        return getParentPackageName(getPageName(clazz));
    }

    /**
     * 获取父包路径
     *
     * @param packageName packageName
     * @return String
     */
    public static String getParentPackageName(String packageName) {
        return packageName.replaceFirst("\\.[^\\.]+$", "");
    }

}
