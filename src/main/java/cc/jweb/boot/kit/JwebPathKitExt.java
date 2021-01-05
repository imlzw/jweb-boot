package cc.jweb.boot.kit;

import io.jboot.app.PathKitExt;

import java.io.File;
import java.lang.reflect.Field;

public class JwebPathKitExt {

    /**
     * 初始化Jfinal与jboot中关于PathKitExt的rootClassPath,
     * 实现自定义classpath到webapp/WEB-INF/classes,习惯于tomcat下的webroot下的WEB-INF/classes结构
     */
    public static void initPathKitExtRootClassPath() {
        try {
            Field jfinalRootClassPath = com.jfinal.server.undertow.PathKitExt.class.getDeclaredField("rootClassPath");
            Field jbootRootClassPath = PathKitExt.class.getDeclaredField("rootClassPath");
            jbootRootClassPath.setAccessible(true);
            jfinalRootClassPath.setAccessible(true);
            String webRootPath = PathKitExt.getWebRootPath();
            String webappClassPath = webRootPath + File.separator + "WEB-INF" + File.separator+ "classes";
            try {
                jfinalRootClassPath.set(null, webappClassPath);
                System.out.println("Jweb init JFinal -> PathKitExt -> rootClassPath: " + webappClassPath);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            try {
                jbootRootClassPath.set(null, webappClassPath);
                System.out.println("Jweb init JBoot -> PathKitExt -> rootClassPath: " + webappClassPath);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
