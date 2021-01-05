package cc.jweb.boot.web.handler;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Action;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import io.jboot.Jboot;
import io.jboot.aop.InterceptorBuilderManager;
import io.jboot.aop.InterceptorCache;
import io.jboot.support.shiro.JbootShiroInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JwebActionInvocation extends Invocation {
    protected static ThreadLocal<List<Interceptor>> invokedInterceptors = ThreadLocal.withInitial(ArrayList::new);
    protected static ThreadLocal<Boolean> controllerInvokeFlag = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static boolean devMode = JFinal.me().getConstants().getDevMode();
    private static InterceptorBuilderManager builderManager = InterceptorBuilderManager.me();
    private Action action;
    private Object target;
    private Object[] args;
    private Interceptor[] inters;
    private int index = 0;
    private Object returnValue;

    public JwebActionInvocation(Action action, Controller controller) {
//        super(action, controller);

        this.action = action;
        this.inters = buildInterceptors(action);
        this.target = controller;

        this.args = action.getParameterGetter().get(action, controller);
    }

    public static List<Interceptor> getInvokedInterceptor() {
        return invokedInterceptors.get();
    }

    public static boolean isControllerInvoked() {
        return controllerInvokeFlag.get();
    }

    public static void clear() {
        invokedInterceptors.get().clear();
        invokedInterceptors.remove();
        controllerInvokeFlag.remove();
    }

    private Interceptor[] buildInterceptors(Action action) {

        InterceptorCache.MethodKey key = InterceptorCache.getMethodKey(action.getControllerClass(), action.getMethod());
        Interceptor[] inters = InterceptorCache.get(key);
        if (inters == null) {
            synchronized (action) {
                inters = InterceptorCache.get(key);
                if (inters == null) {
                    inters = action.getInterceptors();
                    inters = builderManager.build(action.getControllerClass(), action.getMethod(), inters);
                    InterceptorCache.put(key, inters);
                }
            }
        }

        return inters;
    }

    @Override
    public void invoke() {
        if (index < inters.length) {
            Interceptor interceptor = inters[index++];
            if (devMode) {
                invokedInterceptors.get().add(interceptor);
            }
            String isTest = Jboot.configValue("test", "false");
            // 如果在测试环境下，不处理shiro中断器
            if ("true".equalsIgnoreCase(isTest) && interceptor instanceof JbootShiroInterceptor) {
                invoke();
            } else {
                interceptor.intercept(this);
            }
        } else if (index++ == inters.length) {    // index++ ensure invoke action only one time
            try {
                // Invoke the action
                if (devMode) {
                    controllerInvokeFlag.set(true);
                }
                returnValue = action.getMethod().invoke(target, args);
            } catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                if (t == null) {
                    t = e;
                }
                throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    public Interceptor[] getInters() {
        return inters;
    }


    @Override
    public Object getArg(int index) {
        if (index >= args.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return args[index];
    }


    @Override
    public void setArg(int index, Object value) {
        if (index >= args.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        args[index] = value;
    }


    @Override
    public Object[] getArgs() {
        return args;
    }


    @Override
    public <T> T getTarget() {
        return (T) target;
    }

    /**
     * Return the method of this action.
     * <p>
     * You can getMethod.getAnnotations() to get annotation on action method to do more things
     */
    @Override
    public Method getMethod() {
        return action.getMethod();
    }

    /**
     * Return the method name of this action's method.
     */
    @Override
    public String getMethodName() {
        return action.getMethodName();
    }

    /**
     * Get the return value of the target method
     */
    @Override
    public <T> T getReturnValue() {
        return (T) returnValue;
    }


    @Override
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }
    // ---------

    /**
     * Return the controller of this action.
     */
    @Override
    public Controller getController() {
        return (Controller) target;
    }

    /**
     * Return the action key.
     * actionKey = controllerPath + methodName
     */
    @Override
    public String getActionKey() {
        return action.getActionKey();
    }

    /**
     * Return the controller path.
     */
    @Override
    public String getControllerPath() {
        return action.getControllerPath();
    }

    /**
     * 该方法已改名为 getControllerPath()
     */
    @Override
    @Deprecated
    public String getControllerKey() {
        return getControllerPath();
    }

    /**
     * Return view path of this controller.
     */
    @Override
    public String getViewPath() {
        return action.getViewPath();
    }

    /**
     * return true if it is action invocation.
     */
    @Override
    public boolean isActionInvocation() {
        return action != null;
    }

}
