package cc.jweb.boot.app.classloader;

import com.jfinal.server.undertow.hotswap.HotSwapClassLoader;
import com.jfinal.server.undertow.hotswap.HotSwapResolver;

import java.net.URL;

/**
 * 重定义类加载器
 */
public class JwebHotSwapClassLoader extends HotSwapClassLoader {
    public JwebHotSwapClassLoader(URL[] urls, ClassLoader parent, HotSwapResolver hotSwapResolver) {
        super(urls, parent, hotSwapResolver);
    }

    /**
     * 先找本地classLoader里面的资源 ，再找父classLoader的资源
     * @param name
     * @return
     */
    public URL getResource(String name) {
        URL url = findResource(name);
        if (url == null) {
            url = super.getResource(name);
        }
        return url;
    }
}
