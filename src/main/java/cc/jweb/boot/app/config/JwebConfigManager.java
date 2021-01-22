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

package cc.jweb.boot.app.config;

import cc.jweb.boot.Jweb;
import cc.jweb.boot.kit.JwebClassLoaderKit;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.config.SentinelConfigLoader;
import com.jfinal.kit.PathKit;
import io.jboot.Jboot;
import io.jboot.app.config.ConfigUtil;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.config.support.apollo.ApolloConfigManager;
import io.jboot.app.config.support.nacos.NacosConfigManager;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JwebConfigManager {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static JwebConfigManager me;
    JwebClassLoaderKit jwebClassLoaderKit;
    Properties mainProperties;
    //ConfigObje 缓存 class + prefix : object
    private Map<String, Object> configCache = new ConcurrentHashMap<>();

    private JwebConfigManager() {
        jwebClassLoaderKit = new JwebClassLoaderKit(Thread.currentThread().getContextClassLoader(), null);
    }

    public static JwebConfigManager me() {
        if (me == null) {
            me = new JwebConfigManager();
        }
        return me;
    }

    /**
     * 判断类型是否为基本类型(含包装类）
     *
     * @param returnType
     * @return
     */
    private static boolean isBaseType(Class<?> returnType) {
        Class[] baseType = new Class[]{
                String.class,
                Number.class,
                Boolean.class,
                boolean.class,
                Character.class,
                char.class,
                Short.class,
                short.class,
                Long.class,
                long.class,
                float.class,
                Float.class,
                double.class,
                Double.class,
                int.class,
                Integer.class,
                byte.class,
                Byte.class
        };
        for (Class baseClass : baseType) {
            if (returnType.isAssignableFrom(baseClass)) {
                return true;
            }
        }
        return returnType.isPrimitive();
    }

    public static void main(String[] args) {
        System.out.println(isBaseType(String.class));
        System.out.println(isBaseType(Number.class));
        System.out.println(isBaseType(Boolean.class));
        System.out.println(isBaseType(boolean.class));
        System.out.println(isBaseType(char.class));
        System.out.println(isBaseType(short.class));
        System.out.println(isBaseType(int.class));
        System.out.println(isBaseType(Integer.class));
        System.out.println(isBaseType(long.class));
        System.out.println(isBaseType(Long.class));
        System.out.println(isBaseType(float.class));
        System.out.println(isBaseType(Float.class));
        System.out.println(isBaseType(double.class));
        System.out.println(isBaseType(Double.class));
    }

    /**
     * 重新初始化JbootConfigManager的配置数据
     *
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public void resetJbootConfigManager() {
        System.out.println("Jweb reset JbootConfigManager properties!");
        // 通过反射，重新初始化 jbootConfigManager 的配置参数
        try {
            initMainProperties();
            Field mainProperties = JbootConfigManager.class.getDeclaredField("mainProperties");
            mainProperties.setAccessible(true);
            mainProperties.set(JbootConfigManager.me(), this.mainProperties);
            String mode = JbootConfigManager.me().getConfigValue("jboot.app.mode");
            if (ConfigUtil.isNotBlank(mode)) {
                String modePropertiesName = "jboot-" + mode + ".properties";
                this.mainProperties.putAll(getProperties(modePropertiesName));
            }
            // 重新初始化jbootConfigManager.init()中其它的相关初始化代码
            NacosConfigManager.me().init(JbootConfigManager.me());
            ApolloConfigManager.me().init(JbootConfigManager.me());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void initMainProperties() {
        mainProperties = getProperties("jboot.properties");
    }

    private Properties getProperties(String fileName) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = jwebClassLoaderKit.getClassLoader().getResourceAsStream(fileName);
            if (inputStream != null) {
                properties.load(new InputStreamReader(inputStream, DEFAULT_ENCODING));
            }
        } catch (Exception e) {
            System.err.println("Warning: Can not load properties file in classpath, file name :" + fileName);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return properties;
    }

    /**
     * 重置其它配置
     */
    public void resetOtherConfig() {
        String rootClassPath = PathKit.getRootClassPath();
        Properties mainConfig = new Properties();
        Set<String> keys = this.mainProperties.stringPropertyNames();
        for (String key : keys) {
            mainConfig.setProperty(key, JbootConfigManager.me().getConfigValue(key));
        }
        // 重置sentinel.properties配置
        resetSentinelConfig(mainConfig);
//        try {
//            mainConfig.store(new FileOutputStream(rootClassPath + "/jweb.generator.final.properties"), "jweb generator final config file at " + new Date() + " !");
//            System.out.println("Jweb reset final config file: jweb.generator.final.properties!");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 重置sentinel.properties配置
     *
     * @param mainConfig
     */
    private void resetSentinelConfig(Properties mainConfig) {
        Properties properties1 = SentinelConfigLoader.getProperties();
        for (String key : mainConfig.stringPropertyNames()) {
            String property = mainConfig.getProperty(key);
            properties1.setProperty(key, property);
            SentinelConfig.setConfig(key, property);
        }
    }

    /**
     * 获取配置信息，并创建和赋值clazz实例
     *
     * @param clazz  指定的类
     * @param prefix 配置文件前缀
     * @param <T>
     * @return
     */
    public <T> T get(Class<T> clazz, String prefix, String file) {

        /**
         * 开发模式下，热加载会导致由于 Config 是不同的 ClassLoader 而导致异常，
         * 如果走缓存会Class转化异常
         */
        if (Jweb.isDevMode()) {
            return createConfigObject(clazz, prefix, file);
        }

        Object configObject = configCache.get(clazz.getName() + prefix);

        if (configObject == null) {
            synchronized (clazz) {
                if (configObject == null) {
                    configObject = createConfigObject(clazz, prefix, file);
                    configCache.put(clazz.getName() + prefix, configObject);
                }
            }
        }

        return (T) configObject;
    }

    /**
     * 创建一个新的配置对象（Object）
     *
     * @param clazz
     * @param prefix
     * @param file
     * @param <T>
     * @return
     */
    public <T> T createConfigObject(Class<T> clazz, String prefix, String file) {
        Object configObject = ConfigUtil.newInstance(clazz);
        List<Method> setterMethods = ConfigUtil.getClassSetMethods(clazz);
        if (setterMethods != null) {
            for (Method setterMethod : setterMethods) {

                Class<?> parameterType = setterMethod.getParameterTypes()[0];
                String key = buildKey(prefix, setterMethod);
                Object val = null;
                if (!isBaseType(parameterType) && !isCombinationType(parameterType)) {
                    val = createConfigObject(parameterType, key, file);
                } else {
                    String value = getConfigValue(key);
                    if (ConfigUtil.isNotBlank(file)) {
                        JwebProp prop = new JwebProp(file);
                        String filePropValue = JbootConfigManager.me().getConfigValue(prop.getProperties(), key);
                        if (ConfigUtil.isNotBlank(filePropValue)) {
                            value = filePropValue;
                        }
                    }
                    if (ConfigUtil.isNotBlank(value)) {
                        val = ConfigUtil.convert(setterMethod.getParameterTypes()[0], value, setterMethod.getGenericParameterTypes()[0]);
                    }
                }
                if (val != null) {
                    try {
                        setterMethod.invoke(configObject, val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return (T) configObject;
    }

    /**
     * 是否是基本组合类型
     *
     * @param parameterType
     * @return
     */
    private boolean isCombinationType(Class<?> parameterType) {
        if (Map.class.isAssignableFrom(parameterType)) {
            return true;
        } else if (List.class.isAssignableFrom(parameterType)) {
            return true;
        } else if (Set.class.isAssignableFrom(parameterType)) {
            return true;
        } else if (parameterType.isArray()) {
            return true;
        }
        return false;
    }

    private String buildKey(String prefix, Method method) {
        String key = ConfigUtil.firstCharToLowerCase(method.getName().substring(3));
        if (ConfigUtil.isNotBlank(prefix)) {
            key = prefix.trim() + "." + key;
        }
        return key;
    }


    public String getConfigValue(String key) {
        return Jboot.configValue(key);
    }


    class JwebProp {
        private static final String DEFAULT_ENCODING = "UTF-8";
        protected Properties properties = null;

        public JwebProp(String fileName) {
            this(fileName, DEFAULT_ENCODING);
        }

        public JwebProp(String fileName, String encoding) {
            properties = new Properties();
            InputStream inputStream = null;
            try {
                inputStream = jwebClassLoaderKit.getClassLoader().getResourceAsStream(fileName);
                if (inputStream != null) {
                    properties.load(new InputStreamReader(inputStream, encoding));
                }
            } catch (Exception e) {
                System.err.println("Warning: Can not load properties file in classpath, file name: " + fileName);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }


        public JwebProp(File file) {
            properties = new Properties();
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                if (inputStream != null) {
                    properties.load(new InputStreamReader(inputStream, DEFAULT_ENCODING));
                }
            } catch (Exception e) {
                System.err.println("Warning: Can not load properties file: " + file);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        public Properties getProperties() {
            return properties;
        }
    }

}


