package cc.jweb.boot.handler;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * layuiAdmin框架的路由处理程序
 */
public class LayuiAdminRouteHandler extends Handler {
    @Override
    public void handle(String target, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean[] isHandled) {
        // 重定向layuiAdmin的视图地址到jfinal的根地址
        String key = "assets/layuiadmin/dist/views/_";
        String layoutKey = "assets/layuiadmin/dist/views/layout.html";
        if (target.indexOf(layoutKey) >= 0) {
            target = "/layout";
        } else {
            String[] redirestKeys = {key};
            for (String redirestKey : redirestKeys) {
                int preIdx = target.indexOf(key);
                if (preIdx >= 0) {
                    int postIdx = target.indexOf(".html");
                    target = target.substring(preIdx + key.length(), postIdx);
                    break;
                }
            }
        }
        next.handle(target, httpServletRequest, httpServletResponse, isHandled);
    }
}
