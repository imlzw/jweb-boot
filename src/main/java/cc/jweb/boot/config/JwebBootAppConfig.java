package cc.jweb.boot.config;

import cc.jweb.boot.db.JwebArpManager;
import cc.jweb.boot.web.handler.JwebActionHandler;
import cc.jweb.boot.web.render.JwebBootRenderFactory;
import cc.jweb.boot.web.resource.EngineResourceFactory;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.core.listener.JbootAppListenerBase;

/**
 * jweb-boot重写配置
 */
public class JwebBootAppConfig extends JbootAppListenerBase {

    /**
     * 供Shiro插件使用。
     */
    Routes routes;

    @Override
    public void onRouteConfig(Routes me) {
        this.routes = me;
    }


    @Override
    public void onInterceptorConfig(Interceptors me) {
    }

    @Override
    public void onEngineConfig(Engine engine) {
        engine.setSourceFactory(new EngineResourceFactory());
    }

    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {
        handlers.setActionHandler(new JwebActionHandler());
    }

    @Override
    public void onConstantConfig(Constants constants) {
        super.onConstantConfig(constants);
        constants.setRenderFactory(JwebBootRenderFactory.getInstance());
    }

    @Override
    public void onPluginConfig(JfinalPlugins plugins) {
        super.onPluginConfig(plugins);
        JwebArpManager.initArpPlugins(plugins);
    }
}
