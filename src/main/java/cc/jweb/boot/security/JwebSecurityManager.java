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
import cc.jweb.boot.security.annotation.ClearSecurity;
import cc.jweb.boot.security.annotation.RequiresAuthentication;
import cc.jweb.boot.security.annotation.RequiresPermissions;
import cc.jweb.boot.security.annotation.RequiresRoles;
import cc.jweb.boot.security.config.JwebSecurityConfig;
import cc.jweb.boot.security.handler.JwebSecurityHandler;
import cc.jweb.boot.security.interceptor.JwebSecurityInterceptor;
import cc.jweb.boot.security.processer.*;
import cc.jweb.boot.security.session.JwebSecuritySession;
import cc.jweb.boot.security.session.impl.JwebHttpSession;
import cc.jweb.boot.security.session.impl.JwebJwtSession;
import cc.jweb.boot.security.session.perms.JwebNonePermsManager;
import cc.jweb.boot.security.session.perms.JwebPermsManager;
import com.jfinal.aop.Interceptor;
import com.jfinal.config.Routes;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * jweb 安全管理器
 */
public class JwebSecurityManager {

    private final static Map<String, String> typeConvert = new HashMap<>(8);
    // 几种访问权限控制注解
    private static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES = new Class[]{RequiresPermissions.class, RequiresRoles.class};
    private static JwebSecurityManager jwebSecurityManager = new JwebSecurityManager();
    private final String SLASH = "/";
    // actionKey ---> authzProcesser 映射关系缓存
    ConcurrentMap<String, AuthzProcesser> actionProcesserMaps = new ConcurrentHashMap<String, AuthzProcesser>();
    // actionKey ---> Annotation 映射关系缓存
    ConcurrentMap<String, List<Annotation>> actionAnnotationMaps = new ConcurrentHashMap<String, List<Annotation>>();
    private JwebSecurityConfig securityConfig;
    // session 线程变量，当前线程可直接获取到session对象
    private ThreadLocal<JwebSecuritySession> securitySessionLocal = new ThreadLocal();
    private HashMap<String, Class> sessionClassCache = new HashMap<>();
    private JwebPermsManager jwebPermsManager = null;

    private JwebSecurityManager() {
        typeConvert.put("DEFAULT", JwebHttpSession.class.getName());
        typeConvert.put("JWT", JwebJwtSession.class.getName());
    }

    public static JwebSecurityManager me() {
        return jwebSecurityManager;
    }

    public Interceptor getInterceptor() {
        return new JwebSecurityInterceptor(securityConfig);
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
        // 初始化PermsManager实例
        session.setJwebPermsManager(getJwebPermsManager());
        return session;
    }

    /**
     * 获取权限管理器
     * @return
     */
    public JwebPermsManager getJwebPermsManager() {
        if (jwebPermsManager == null) {
            String manager = securityConfig.getPerms().getManager();
            if (manager != null) {
                Class<?> aClass = null;
                try {
                    aClass = Class.forName(manager);
                    jwebPermsManager = (JwebPermsManager)aClass.newInstance();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        return jwebPermsManager;
    }



    public JwebSecuritySession getSession() {
        return securitySessionLocal.get();
    }

    /**
     * 扫描路由，
     * <p>
     * 解析注解配置信息，生成缓存映射对象
     *
     * @param routeList
     */
    public void injectRoutes(List<Routes.Route> routeList) {
        Set<String> excludedMethodName = buildExcludedMethodName();
        for (Routes.Route route : routeList) {
            Class<? extends Controller> controllerClass = route.getControllerClass();
            ClearSecurity controllerClearSecurity = controllerClass.getAnnotation(ClearSecurity.class);
            String controllerKey = route.getControllerPath();
            // 获取Controller的所有JwebSecurity注解。
            List<Annotation> controllerAnnotations = getAuthzAnnotations(controllerClass);
            // 逐个遍历方法。
            Method[] methods = controllerClass.getMethods();
            for (Method method : methods) {
                // 排除掉Controller基类的所有方法，并且只关注public方法。
                if (Modifier.isPublic(method.getModifiers()) && !excludedMethodName.contains(method.getName())) {
                    // 构建ActionKey，参考ActionMapping中实现
                    String actionKey = createActionKey(controllerClass, method, controllerKey);
                    // 若该方法上存在ClearJwebSecurity注解，则对该action不进行访问控制检查。
                    ArrayList<Annotation> annotations = new ArrayList<>();
                    ClearSecurity clearSecurity = method.getAnnotation(ClearSecurity.class);
                    if (controllerClearSecurity != null || clearSecurity != null) {
                        annotations.add(controllerClearSecurity != null ? controllerClearSecurity : clearSecurity);
                        actionAnnotationMaps.put(actionKey, annotations);
                        continue;
                    }
                    // 获取方法的所有JwebSecurity注解。
                    List<Annotation> methodAnnotations = getAuthzAnnotations(method);
                    annotations.addAll(controllerAnnotations);
                    annotations.addAll(methodAnnotations);
                    actionAnnotationMaps.put(actionKey, annotations);
                    // 依据Controller的注解和方法的注解来生成访问控制处理器。
                    AuthzProcesser authzProcesser = createAuthzProcesser(controllerAnnotations, methodAnnotations);
                    // 生成访问控制处理器成功。
                    if (authzProcesser != null) {
                        // 添加映射
                        actionProcesserMaps.put(actionKey, authzProcesser);
                    }
                }
            }
        }
    }

    /**
     * 依据Controller的注解和方法的注解来生成访问控制处理器。
     *
     * @param controllerAnnotations Controller的注解
     * @param methodAnnotations     方法的注解
     * @return 访问控制处理器
     */
    private AuthzProcesser createAuthzProcesser(List<Annotation> controllerAnnotations,
                                                List<Annotation> methodAnnotations) {

        // 没有注解
        if (controllerAnnotations.size() == 0 && methodAnnotations.size() == 0) {
            return null;
        }
        // 至少有一个注解
        List<AuthzProcesser> authzProcessers = new ArrayList<AuthzProcesser>(AUTHZ_ANNOTATION_CLASSES.length);
        for (int index = 0; index < AUTHZ_ANNOTATION_CLASSES.length; index++) {
            authzProcessers.add(null);
        }

        // 逐个扫描注解，若是相应的注解则在相应的位置赋值。
        scanAnnotation(authzProcessers, controllerAnnotations);
        // 逐个扫描注解，若是相应的注解则在相应的位置赋值。函数的注解优先级高于Controller
        scanAnnotation(authzProcessers, methodAnnotations);

        // 去除空值
        List<AuthzProcesser> finalAuthzProcessers = new ArrayList<AuthzProcesser>();
        for (AuthzProcesser a : authzProcessers) {
            if (a != null) {
                finalAuthzProcessers.add(a);
            }
        }
        authzProcessers = null;
        // 存在多个，则构建组合AuthzHandler
        if (finalAuthzProcessers.size() > 1) {
            return new CompositeAuthzProcesser(finalAuthzProcessers);
        }
        // 一个的话直接返回
        return finalAuthzProcessers.get(0);
    }

    /**
     * 逐个扫描注解，若是相应的注解则在相应的位置赋值。
     * 注解的处理是有顺序的，依次为RequiresRoles，RequiresPermissions，RequiresAuthentication
     *
     * @param authzArray
     * @param annotations
     */
    private void scanAnnotation(List<AuthzProcesser> authzArray, List<Annotation> annotations) {
        if (null == annotations || 0 == annotations.size()) {
            return;
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof RequiresRoles) {
                authzArray.set(0, new RoleAuthzProcesser(annotation));
            } else if (annotation instanceof RequiresPermissions) {
                authzArray.set(1, new PermissionAuthzProcesser(annotation));
            } else if (annotation instanceof RequiresAuthentication) {
                authzArray.set(2, AuthenticatedAuthzProcesser.me());
            }
        }
    }

    /**
     * 构建actionkey，参考ActionMapping中的实现。
     *
     * @param controllerClass
     * @param method
     * @param controllerKey
     * @return
     */
    private String createActionKey(Class<? extends Controller> controllerClass, Method method, String controllerKey) {
        String methodName = method.getName();
        String actionKey = "";

        ActionKey ak = method.getAnnotation(ActionKey.class);
        if (ak != null) {
            actionKey = ak.value().trim();
            if ("".equals(actionKey))
                throw new IllegalArgumentException(controllerClass.getName() + "." + methodName
                        + "(): The argument of ActionKey can not be blank.");
            if (!actionKey.startsWith(SLASH))
                actionKey = SLASH + actionKey;
        } else if (methodName.equals("index")) {
            actionKey = controllerKey;
        } else {
            actionKey = controllerKey.equals(SLASH) ? SLASH + methodName : controllerKey + SLASH + methodName;
        }
        return actionKey;
    }

    /**
     * 返回该Controller的所有访问控制注解
     *
     * @return
     */
    private List<Annotation> getAuthzAnnotations(Class<? extends Controller> targetClass) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        for (Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES) {
            Annotation a = targetClass.getAnnotation(annClass);
            if (a != null) {
                annotations.add(a);
            }
        }
        return annotations;
    }

    /**
     * 返回该方法的所有访问控制注解
     *
     * @param method
     * @return
     */
    private List<Annotation> getAuthzAnnotations(Method method) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        for (Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES) {
            Annotation a = method.getAnnotation(annClass);
            if (a != null) {
                annotations.add(a);
            }
        }
        return annotations;
    }

    /**
     * 从Controller方法中构建出需要排除的方法列表
     *
     * @return
     */
    private Set<String> buildExcludedMethodName() {
        Set<String> excludedMethodName = new HashSet<String>();
        Method[] methods = Controller.class.getMethods();
        for (Method m : methods) {
            if (Modifier.isPublic(m.getModifiers())) {
                excludedMethodName.add(m.getName());
            }
        }
        return excludedMethodName;
    }

    /**
     * 该controller上是否有ClearSecurity注解
     *
     * @param controller
     * @return
     */
    public boolean isClearSecurityAnnotationPresent(Class controller) {
        Annotation a = controller.getAnnotation(ClearSecurity.class);
        if (a != null) {
            return true;
        }
        return false;
    }

    /**
     * 该方法上是否有ClearSecurity注解
     *
     * @param method
     * @return
     */
    public boolean isClearSecurityAnnotationPresent(Method method) {
        Annotation a = method.getAnnotation(ClearSecurity.class);
        if (a != null) {
            return true;
        }
        return false;
    }

    public AuthzProcesser getAuthzProcesser(String actionKey) {
        return actionProcesserMaps.get(actionKey);
    }

    /**
     * 判断actionKey是否清除安全注解
     *
     * @param actionKey
     * @return
     */
    public boolean isActionClearSecurity(String actionKey) {
        List<Annotation> annotations = actionAnnotationMaps.get(actionKey);
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof ClearSecurity) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断action是否有认证注解
     *
     * @param actionKey
     * @return
     */
    public boolean isActionRequiredAuthc(String actionKey) {
        List<Annotation> annotations = actionAnnotationMaps.get(actionKey);
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof RequiresAuthentication) {
                    return true;
                }
            }
        }
        return false;
    }
}
