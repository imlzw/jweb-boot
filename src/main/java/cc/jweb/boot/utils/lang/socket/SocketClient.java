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
import cc.jweb.boot.utils.lang.interf.Disposable;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * socket客户端工具类
 *
 * @author ag777
 * @version create on 2018年05月30日,last modify at 2020年08月25日
 */
public class SocketClient implements Disposable {

    private Socket socket;
    private PrintWriter out = null;
    private BufferedReader in = null;

    public SocketClient(Socket socket, OutputStream out, InputStream in) {
        this.socket = socket;
        this.out = new PrintWriter(out);
        this.in = new BufferedReader(new InputStreamReader(in));
    }

    /**
     * 创建套接字
     *
     * @param ip   ip
     * @param port port
     * @return
     * @throws UnknownHostException UnknownHostException
     * @throws IOException          IOException
     */
    public static SocketClient build(String ip, int port) throws UnknownHostException, IOException {
        Socket s = new Socket(ip, port);
        return new SocketClient(s, s.getOutputStream(), s.getInputStream());
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        SocketClient client = build("127.0.0.1", 1523);
        System.out.println(client.sendMsgForReply("123"));
        System.out.println(client.sendMsgForReply("结束"));
        System.out.println(client.sendMsgForReply("456"));
        client.dispose();
    }

    /**
     * 关闭连接,释放资源
     */
    @Override
    public void dispose() {
        IOUtils.close(out); // 关闭Socket输出流
        IOUtils.close(in); // 关闭Socket输入流
        IOUtils.close(socket); // 关闭Socket
        out = null;
        in = null;
        socket = null;
    }

    /**
     * 接收消息(行)
     *
     * @return
     * @throws IOException IOException
     */
    public String readLine() throws IOException {
        return in.readLine();
    }

    /**
     * 发送消息
     *
     * @param msg msg
     * @throws IOException IOException
     */
    public void sendMsg(String msg) throws IOException {
        out.println(msg);
        out.flush();
    }

    /**
     * 发送一条消息，返回紧接着接收到的下一条消息
     *
     * @param msg msg
     * @return
     * @throws IOException IOException
     */
    public String sendMsgForReply(String msg) throws IOException {
        sendMsg(msg);
        return readLine();
    }
}