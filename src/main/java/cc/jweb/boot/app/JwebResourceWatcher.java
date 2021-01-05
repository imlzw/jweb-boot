package cc.jweb.boot.app;

import cc.jweb.boot.utils.file.FileUtils;
import com.jfinal.server.undertow.UndertowKit;
import io.jboot.app.JbootResourceLoader;
import io.jboot.app.config.JbootConfigManager;
import io.jboot.utils.StrUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * web资源监听器
 * <p>
 * 解决jboot原生资源监听性能问题
 *
 * @author imlzw@vip.qq.com
 * @platform www.jweb.cc
 */
public class JwebResourceWatcher extends Thread {

    // protected int watchingInterval = 1000;	// 1900 与 2000 相对灵敏
    protected int watchingInterval = 500;
    protected List<Path> watchingPaths;
    protected volatile boolean running = true;
    private WatchKey watchKey;
    private String resourcePathName;

    public JwebResourceWatcher() {
        String pathName = JbootConfigManager.me().getConfigValue("jboot.app.resourcePathName");
        this.resourcePathName = StrUtil.obtainDefault(pathName, "webapp");
        this.watchingPaths = new ArrayList<Path>();
    }

    public JwebResourceWatcher(String resourcePathName) {
        this.resourcePathName = StrUtil.requireNonBlank(resourcePathName, "Resource path name must not be blank.");
        this.watchingPaths = new ArrayList<Path>();
    }

    public void start() {
        try {
            URL url = JbootResourceLoader.class.getClassLoader().getResource("");
            if (url == null || url.toString().endsWith(".jar!/")) {
                return;
            }
            String classPath = url.toURI().getPath();
            File srcRootPath = new File(classPath, "../..").getCanonicalFile();
            if (new File(srcRootPath.getParent(), "pom.xml").exists()) {
                srcRootPath = srcRootPath.getParentFile();
            }
            findResourcesPath(srcRootPath);
            doRun(classPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doRun(String classPath) throws IOException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        addShutdownHook(watcher);

        for (Path path : watchingPaths) {
            path.register(
                    watcher,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE
            );
        }

        System.err.println("JwebResourceWather started, Watched resource path name : " + resourcePathName);

        while (running) {
            try {
                // watchKey = watcher.poll(watchingInterval, TimeUnit.MILLISECONDS);	// watcher.take(); 阻塞等待
                // 比较两种方式的灵敏性，或许 take() 方法更好，起码资源占用少，测试 windows 机器上的响应
                watchKey = watcher.take();
                if (watchKey == null) {
                    // System.out.println(System.currentTimeMillis() / 1000);
                    continue;
                }
            } catch (Throwable e) {                        // 控制台 ctrl + c 退出 JVM 时也将抛出异常
                running = false;
                if (e instanceof InterruptedException) {    // 另一线程调用 hotSwapWatcher.interrupt() 抛此异常
                    Thread.currentThread().interrupt();    // Restore the interrupted status
                }
                break;
            }
            try {
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                for (WatchEvent<?> event : watchEvents) {
                    Path path = (Path) event.context();
                    Path dir = (Path) watchKey.watchable();
                    String absolutePath = dir.toString() + File.separator + path.toFile();
                    // main/webapp/
                    String webappPath = "main" + File.separator + resourcePathName + File.separator;
                    int indexOf = absolutePath.indexOf(webappPath);
                    String target = classPath + resourcePathName + File.separator + absolutePath.substring(indexOf + webappPath.length());
                    // 删除
                    if (StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind())) {
                        try {
                            FileUtils.delete(target);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        System.err.println("JwebResourceWatcher delete " + target);
                    } else if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
                        try {
                            FileUtils.copy(absolutePath, target);
                        } catch (Exception e) {
                        }
                        System.err.println("JwebResourceWatcher create " + target);
                    } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {
                        try {
                            FileUtils.copy(absolutePath, target);
                        } catch (Exception e) {
                        }
                        System.err.println("JwebResourceWatcher modify " + target);
                    } else {
                        // do nothing...
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                resetWatchKey();
            }
        }
    }


    private void resetWatchKey() {
        if (watchKey != null) {
            watchKey.reset();
            watchKey = null;
        }
    }

    private void findResourcesPath(File root) {
        File[] dirs = root.listFiles(pathname -> pathname.isDirectory());
        if (dirs == null || dirs.length == 0) {
            return;
        }
        for (File dir : dirs) {

            File parentFile = dir.getParentFile();
            if (parentFile == null) {
                return;
            }

            if (dir.getName().equals(resourcePathName)
                    && parentFile.getName().equals("main")) {
                initWatchPaths(dir);
            } else {
                findResourcesPath(dir);
            }
        }
    }

    private void initWatchPaths(File file) {
        if (file.isDirectory()) {
            watchingPaths.add(Paths.get(file.getAbsolutePath()));
            File[] fileList = file.listFiles();
            for (File f : fileList) {
                initWatchPaths(f);
            }
        }
    }


    /**
     * 添加关闭钩子在 JVM 退出时关闭 WatchService
     * <p>
     * 注意：addShutdownHook 方式添加的回调在 kill -9 pid 强制退出 JVM 时不会被调用
     * kill 不带参数 -9 时才回调
     */
    protected void addShutdownHook(WatchService watcher) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                watcher.close();
            } catch (Throwable e) {
                UndertowKit.doNothing(e);
            }
        }));
    }

}
