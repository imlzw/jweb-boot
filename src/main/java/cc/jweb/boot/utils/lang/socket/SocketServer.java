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

package cc.jweb.boot.utils.lang.socket;

import cc.jweb.boot.utils.lang.IOUtils;
import cc.jweb.boot.utils.lang.StringUtils;
import cc.jweb.boot.utils.lang.collection.MapUtils;
import cc.jweb.boot.utils.lang.interf.Disposable;
import cc.jweb.boot.utils.lang.socket.model.Handler;
import cc.jweb.boot.utils.lang.socket.model.Session;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * socket服务端工具类
 *
 * @author ag777
 * @version create on 2018年05月30日,last modify at 2020年08月25日
 */
public class SocketServer implements Disposable {

    private ServerSocket server;
    private int port;
    private boolean isRunning;

    private Map<String, Session> sessionMap;
    private ThreadGroup tg;

    public SocketServer(ServerSocket server, Handler handler) {
        this.server = server;
        this.port = server.getLocalPort();
        sessionMap = MapUtils.newConcurrentHashMap();
        isRunning = true;
        handler.onSeverCreate(port);
        tg = new ThreadGroup("socket-server");

        while (isRunning) {
            try {
                Socket socket = server.accept();
                String sessionId = StringUtils.uuid();
                Session session = new Session(sessionId, socket);
                /*
                 *连接建立判断是否需要断开连接
                 */
                if (!handler.onPreConnect(socket, sessionId)) {
                    IOUtils.close(socket);
                    continue;
                }

                /*
                 * 连接建立后执行
                 */
                handler.onConnect(socket, sessionId);

                /*
                 * 允许本次连接新建线程执行处理客户端传来的信息
                 */
                Thread sessionThread = new Thread(tg, () -> {
                    try {
                        session.handle(handler);
                    } catch (InterruptedException ex) {
                        //中断
                    } catch (Throwable ex) {
                        handler.onErr(socket, sessionId, ex);
                    } finally {
                        sessionMap.remove(sessionId);
                        handler.onDisConnect(socket, sessionId);
                    }
                });

                sessionMap.put(session.getId(), session);
                sessionThread.start();

            } catch (Exception ex) {
                handler.onServerErr(ex);
            }
        }

        IOUtils.close(server);
        handler.onServerDispose(port);
    }

    /**
     * @param port    端口号
     * @param handler 处理器
     * @return
     * @throws BindException Address already in use: JVM_Bind
     * @throws IOException   IOException
     */
    public static SocketServer build(int port, Handler handler) throws BindException, IOException {
        ServerSocket server = new ServerSocket(port);
        return new SocketServer(server, handler);
    }

    public static void main(String[] args) throws IOException {
        build(1523, new Handler() {

            @Override
            public boolean onPreConnect(Socket socket, String sessionId) {
                System.out.println("连接中:" + sessionId);
                return true;
            }

            @Override
            public void onConnect(Socket socket, String sessionId) {
                System.out.println("建立:" + sessionId);
            }

            @Override
            public void onDisConnect(Socket socket, String sessionId) {
                super.onDisConnect(socket, sessionId);
                System.out.println("断开:" + sessionId);
            }

            @Override
            public String handle(String msg, String sessionId) {
                return "得到的消息" + msg;
            }

            @Override
            public boolean hasNext(String msg) {
                return !"结束".equals(msg);
            }
        });
        System.out.println("服务端建立");
    }

    public ServerSocket getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public Map<String, Session> getSessionMap() {
        return sessionMap;
    }

    /**
     * 停止监听客户端请求
     */
    @Override
    public void dispose() {
        isRunning = false;
        try {
            server.close();
            tg.interrupt();
        } catch (IOException e) {
//			e.printStackTrace();
        }


    }

}
