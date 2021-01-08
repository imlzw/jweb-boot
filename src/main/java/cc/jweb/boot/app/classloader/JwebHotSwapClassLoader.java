package cc.jweb.boot.app.classloader;

import com.jfinal.server.undertow.hotswap.HotSwapClassLoader;
import com.jfinal.server.undertow.hotswap.HotSwapResolver;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.WeakHashMap;
import java.util.jar.JarFile;

/**
 * 重定义类加载器
 */
public class JwebHotSwapClassLoader extends HotSwapClassLoader {
    private WeakHashMap<Closeable, Void> closeables = new WeakHashMap<>();

    public JwebHotSwapClassLoader(URL[] urls, ClassLoader parent, HotSwapResolver hotSwapResolver) {
        super(urls, parent, hotSwapResolver);
    }

    /**
     * 先找本地classLoader里面的资源 ，再找父classLoader的资源
     *
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

    public InputStream getResourceAsStream(String name) {
        URL url = getResource(name);
        try {
            if (url == null) {
                return null;
            }
            URLConnection urlc = url.openConnection();
            InputStream is = urlc.getInputStream();
            if (urlc instanceof JarURLConnection) {
                JarURLConnection juc = (JarURLConnection) urlc;
                JarFile jar = juc.getJarFile();
                synchronized (closeables) {
                    if (!closeables.containsKey(jar)) {
                        closeables.put(jar, null);
                    }
                }
            } else if (urlc instanceof sun.net.www.protocol.file.FileURLConnection) {
                synchronized (closeables) {
                    closeables.put(is, null);
                }
            }
            return is;
        } catch (IOException e) {
            return null;
        }
    }
}
