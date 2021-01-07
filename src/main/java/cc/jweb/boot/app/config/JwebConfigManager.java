package cc.jweb.boot.app.config;

import cc.jweb.boot.kit.JwebClassLoaderKit;
import io.jboot.app.config.ConfigUtil;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.app.config.support.apollo.ApolloConfigManager;
import io.jboot.app.config.support.nacos.NacosConfigManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Properties;

public class JwebConfigManager {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static JwebConfigManager me;
    JwebClassLoaderKit jwebClassLoaderKit;
    Properties mainProperties;

    private JwebConfigManager() {
        jwebClassLoaderKit = new JwebClassLoaderKit(Thread.currentThread().getContextClassLoader(), null);
    }

    public static JwebConfigManager me() {
        if (me == null) {
            me  = new JwebConfigManager();
        }
        return me;
    }

    /**
     * 重新初始化JbootConfigManager的配置数据
     * 
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public void resetJbootConfigManager(){
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

}
