package cc.jweb.boot.aop.cglib;

import com.jfinal.proxy.ProxyFactory;

public class JwebCglibProxyFactory extends ProxyFactory {
    @Override
    public <T> T get(Class<T> target) {
        return (T) net.sf.cglib.proxy.Enhancer.create(target, new JwebCglibCallback());
    }

}
