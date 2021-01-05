package cc.jweb.boot.web.handler;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import io.jboot.web.handler.JbootActionHandler;

public class JwebActionHandler extends JbootActionHandler {
    /**
     * 方便子类复写、从而可以实现 自定义 Invocation 的功能
     *
     * @param action
     * @param controller
     * @return
     */
    public Invocation getInvocation(Action action, Controller controller) {
        return new JwebActionInvocation(action, controller);
    }


}
