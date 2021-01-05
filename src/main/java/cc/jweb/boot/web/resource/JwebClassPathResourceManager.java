package cc.jweb.boot.web.resource;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.URLResource;

import java.io.IOException;
import java.net.URL;

/**
 * jweb类路径资源管理器
 * 参考自：io.undertow.server.handlers.resource.ClassPathResourceManager
 */
public class JwebClassPathResourceManager extends ClassPathResourceManager {

    public JwebClassPathResourceManager(ClassLoader loader, Package p) {
        super(loader, p);
    }

    public JwebClassPathResourceManager(ClassLoader classLoader, String prefix) {
        super(classLoader, prefix);
    }

    public JwebClassPathResourceManager(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public Resource getResource(final String path) throws IOException {
        Resource resource = super.getResource(path);
        // 如果是jar资源，变更Resource类型
        return resource;
    }

}
