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
