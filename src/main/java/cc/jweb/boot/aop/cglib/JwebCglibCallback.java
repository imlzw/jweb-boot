package cc.jweb.boot.aop.cglib;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.InterceptorManager;
import com.jfinal.aop.Invocation;
import io.jboot.aop.InterceptorBuilderManager;
import io.jboot.aop.InterceptorCache;
import io.jboot.utils.ClassUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class JwebCglibCallback implements MethodInterceptor {
    private static final Set<String> excludedMethodName = buildExcludedMethodName();
    private static final InterceptorManager interManager = InterceptorManager.me();
    private static final InterceptorBuilderManager builderManager = InterceptorBuilderManager.me();

    private static final Set<String> buildExcludedMethodName() {
        Set<String> excludedMethodName = new HashSet<String>(64, 0.25F);
        Method[] methods = Object.class.getDeclaredMethods();
        for (Method m : methods) {
            excludedMethodName.add(m.getName());
        }
        // getClass() registerNatives() can not be enhanced
        // excludedMethodName.remove("getClass");
        // excludedMethodName.remove("registerNatives");
        return excludedMethodName;
    }

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (excludedMethodName.contains(method.getName())) {
            return methodProxy.invokeSuper(target, args);
        }

        Class<?> targetClass = ClassUtil.getUsefulClass(target.getClass());

        InterceptorCache.MethodKey key = InterceptorCache.getMethodKey(targetClass, method);
        Interceptor[] inters = InterceptorCache.get(key);
        if (inters == null) {
            synchronized (method) {
                inters = InterceptorCache.get(key);
                if (inters == null) {

                    inters = interManager.buildServiceMethodInterceptor(targetClass, method);
                    inters = builderManager.build(targetClass, method, inters);

                    InterceptorCache.put(key, inters);
                }
            }
        }


        Invocation invocation = new Invocation(target, method, inters,
                x -> methodProxy.invokeSuper(target, x), args);

        invocation.invoke();
        return invocation.getReturnValue();
    }
}
