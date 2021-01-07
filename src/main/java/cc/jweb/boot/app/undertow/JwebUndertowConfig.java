package cc.jweb.boot.app.undertow;

import cc.jweb.boot.kit.JwebClassLoaderKit;
import com.jfinal.server.undertow.PropExt;
import com.jfinal.server.undertow.hotswap.ClassLoaderKit;
import io.jboot.app.undertow.JbootUndertowConfig;
import io.undertow.Undertow;

import java.lang.reflect.Field;

public class JwebUndertowConfig extends JbootUndertowConfig {
    public JwebUndertowConfig(Class<?> jfinalConfigClass) {
        super(jfinalConfigClass);
    }

    public JwebUndertowConfig(String jfinalConfigClass) {
        super(jfinalConfigClass);
        this.resetClassLoaderKit();
    }


    protected ClassLoaderKit getClassLoaderKit() {
        if (classLoaderKit == null) {
            classLoaderKit = new ClassLoaderKit(Undertow.class.getClassLoader(), getHotSwapResolver());
        }
        return classLoaderKit;
    }

    /**
     * 重置classLoadKit对象为JwebClassLoadKit对象
     *
     */
    private void resetClassLoaderKit() {
        classLoaderKit = new JwebClassLoaderKit(Thread.currentThread().getContextClassLoader(), getHotSwapResolver());
        System.out.println("Jweb reset JFinal -> UndertowConfig -> classLoaderKit: " + classLoaderKit);
    }

    public JwebUndertowConfig(Class<?> jfinalConfigClass, String undertowConfig) {
        super(jfinalConfigClass, undertowConfig);
    }

    public JwebUndertowConfig(String jfinalConfigClass, String undertowConfig) {
        super(jfinalConfigClass, undertowConfig);
    }

    /**
     * 重置PropExt对象的classLoader
     */
    public static void resetPropExtClassLoaderKit(){
        Field classLoaderKit = null;
        try {
            classLoaderKit = PropExt.class.getDeclaredField("classLoaderKit");
            classLoaderKit.setAccessible(true);
            try {
                JwebClassLoaderKit jwebClassLoaderKit = new JwebClassLoaderKit(Thread.currentThread().getContextClassLoader(), null);
                classLoaderKit.set(null, jwebClassLoaderKit);
                System.out.println("Jweb reset JFinal -> UndertowConfig -> PropExt -> classLoaderKit: " + jwebClassLoaderKit);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


}
