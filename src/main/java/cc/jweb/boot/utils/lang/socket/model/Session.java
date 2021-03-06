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

package cc.jweb.boot.utils.lang.socket.model;

import cc.jweb.boot.utils.lang.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 客户端请求处理类,socket服务端配套
 *
 * @author ag777
 * @version create on 2018年05月30日,last modify at 2020年08月25日
 */
public class Session {

    private String id;
    private Socket socket;

    public Session(String id, Socket socket) {
        this.id = id;
        this.socket = socket;
    }

    public String getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    public void handle(Handler handler) throws IOException, InterruptedException {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            while (true) {
                String msg = handler.readMsg(in);    //收到的信息
                if (msg == null) {
                    break;
                }
                String outMsg = handler.handle(msg, id);    //准备回复的信息
                out.println(outMsg);
                out.flush();
                if (!handler.hasNext(msg)) {
                    break;
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
            socket.close();    //断开连接
        }
        System.out.println("通信结束:" + id);
    }

}
