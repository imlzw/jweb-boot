package cc.jweb.boot.components.gateway;

/**
 * jweb网关中断器
 */
public class JwebGatewayInterceptor {

    public void intercept(JwebGatewayInvocation inv) {
        inv.invoke();
    }
}
