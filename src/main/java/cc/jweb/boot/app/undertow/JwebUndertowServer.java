package cc.jweb.boot.app.undertow;

import com.jfinal.server.undertow.UndertowConfig;
import io.jboot.app.undertow.JbootUndertowServer;

public class JwebUndertowServer extends JbootUndertowServer {

    public JwebUndertowServer(UndertowConfig undertowConfig) {
        super(undertowConfig);
    }

    public synchronized void start() {
        super.start();
        String msg = "Jweb Started! Visit http://" + config.getHost() + ":" + config.getPort() + getContextPathInfo();
        if (config.isSslEnable()) {
            msg = msg + ", https://" + config.getHost() + ":" + config.getSslConfig().getPort() + getContextPathInfo();
        }
        System.out.println(msg);
    }

    @Override
    public synchronized void restart() {
        if (started) {
            started = false;
        } else {
            return;
        }

        try {
            System.err.println("\nLoading changes ......");
            long start = System.currentTimeMillis();

            try {
                doStop();
                System.out.println("webapp is stop?" + !started);
            } catch (Exception e) {
                System.err.println("Error stop webapp after change in watched files");
                e.printStackTrace();
            }
            config.replaceClassLoader();
            try {
                doStart();
            } catch (Exception exception) {
                System.err.println("Error start webapp after change in watched files");
                exception.printStackTrace();
            }

            System.err.println("Loading complete in " + getTimeSpent(start) + " seconds (^_^)\n");

        } catch (Exception e) {
            System.err.println("Error restarting webapp after change in watched files");
            e.printStackTrace();
        } finally {
            while (!started) {
                System.out.println("try restart undertow ...");
                doStart();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();
    }
}
