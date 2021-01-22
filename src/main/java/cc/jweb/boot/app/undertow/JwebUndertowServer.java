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
