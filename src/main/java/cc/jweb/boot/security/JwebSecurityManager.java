/*
 * Copyright (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.jweb.boot.security;

import cc.jweb.boot.Jweb;
import cc.jweb.boot.security.config.JwebSecurityConfig;
import cc.jweb.boot.security.handler.JwebSecurityHandler;
import cc.jweb.boot.security.interceptor.JwebSecurityInterceptor;
import cc.jweb.boot.security.session.JwebSecuritySession;
import cc.jweb.boot.security.session.impl.JwebHttpSession;
import cc.jweb.boot.security.session.impl.JwebJwtSession;
import com.jfinal.aop.Interceptor;
import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * jweb 安全管理器
 */
public class JwebSecurityManager {

    private final static Map<String, String> typeConvert = new HashMap<>(8);
    private static JwebSecurityManager jwebSecurityManager = new JwebSecurityManager();
    private JwebSecurityConfig securityConfig;
    // session 线程变量，当前线程可直接获取到session对象
    private ThreadLocal<JwebSecuritySession> securitySessionLocal = new ThreadLocal();
    private HashMap<String, Class> sessionClassCache = new HashMap<>();

    private JwebSecurityManager() {
        typeConvert.put("DEFAULT", JwebHttpSession.class.getName());
        typeConvert.put("JWT", JwebJwtSession.class.getName());
    }

    public static JwebSecurityManager me() {
        return jwebSecurityManager;
    }

    public static Interceptor getInterceptor() {
        return new JwebSecurityInterceptor();
    }

    public Handler getSecurityHandler() {
        JwebSecurityHandler jwebSecurityHandler = new JwebSecurityHandler(securityConfig);
        return jwebSecurityHandler;
    }

    /**
     * 初始化
     */
    public void init() {
        // 加载配置
        this.securityConfig = Jweb.config(JwebSecurityConfig.class, "jweb.security");
    }

    /**
     * 初始化session对象
     *
     * @param request
     * @param response
     * @return
     */
    public JwebSecuritySession initSession(HttpServletRequest request, HttpServletResponse response, JwebSecurityConfig config) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String sessionType = securityConfig.getSessionType();
        if (typeConvert.get(sessionType) != null) {
            sessionType = typeConvert.get(sessionType);
        }
        Class<?> sessionClass = sessionClassCache.get(sessionType);
        if (sessionClass == null) {
            sessionClass = Class.forName(sessionType);
            sessionClassCache.put(sessionType, sessionClass);
        }
        if (!JwebSecuritySession.class.isAssignableFrom(sessionClass)) {
            System.out.println("Jweb SecurityManager InitSession Error! ");
            throw new ClassCastException(sessionClass.getName() + " is not impl from " + JwebSecuritySession.class.getName());
        }
        Constructor<?> declaredConstructor = sessionClass.getDeclaredConstructor(HttpServletRequest.class, HttpServletResponse.class, JwebSecurityConfig.class);
        JwebSecuritySession session = (JwebSecuritySession) declaredConstructor.newInstance(request, response, config);
        securitySessionLocal.set(session);
        return session;
    }

    public JwebSecuritySession getSession() {
        return securitySessionLocal.get();
    }
}
