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

package cc.jweb.boot.utils.lang.cmd;

import cc.jweb.boot.utils.lang.IOUtils;
import cc.jweb.boot.utils.lang.SystemUtils;
import cc.jweb.boot.utils.lang.function.ConsumerE;
import cc.jweb.boot.utils.lang.model.Charsets;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 执行控制台(cmd/shell)命令的工具类
 * <p>
 * 执行一个cmd命令会产生三个流（input/output/err），其中一个不处理就有可能产生程序挂起问题，永远不可能得到返回了
 * </p>
 *
 * @author ag777
 * @version create on 2018年07月04日,last modify at 2020年07月20日
 */
public abstract class AbstractCmdUtils {

    private Charset charsetDefault;

    public AbstractCmdUtils() {
        if (SystemUtils.isWindows()) {    //windows用gbk读取
            this.charsetDefault = Charsets.GBK;
        } else {    //linux用utf8读取
            this.charsetDefault = Charsets.UTF_8;
        }

    }

    public AbstractCmdUtils(Charset charset) {
        this.charsetDefault = charset;
    }

    /**
     * 读取cmd返回时先关输出流，开子线程读错误流
     * <p>
     * 关于OutputStream,程序用不到,直接在开始的时候就应该close掉.对于errStream应该分别用一个线程来读取出IO流中的内容.
     * 注意:必须使用线程,否则依然会阻塞.
     * </p>
     * <p>
     * 参考文章:http://xiaohuafyle.iteye.com/blog/1562786
     *
     * @param pro pro
     */
    public static void pre(Process pro) {
        closeOutput(pro);
        readErr(pro);
    }

    /**
     * 关闭输出流
     *
     * @param pro pro
     */
    protected static void closeOutput(Process pro) {
        try {
            pro.getOutputStream().close();
        } catch (IOException e) {
        }
    }

    /**
     * 子线程读取错误流
     *
     * @param pro 进程
     */
    protected static void readErr(Process pro) {
        try {
            Thread t = new Thread() {
                public void run() {
                    try {
                        IOUtils.readLines(pro.getErrorStream(), "gb2312");
                    } catch (IOException e) {
                    }
                }
            };
            t.start();
        } catch (Exception ex) {
        }
    }

    /**
     * 子线程读取输出流
     *
     * @param pro pro
     */
    protected static void readOutput(Process pro) {
        try {
            Thread t = new Thread() {
                public void run() {
                    try {
                        IOUtils.readLines(pro.getInputStream(), "gb2312");
                    } catch (IOException e) {
                    } finally {
                    }
                }
            };
            t.start();
        } catch (Exception ex) {
        }
    }

    public Charset getCharset() {
        return charsetDefault;
    }

    public void setCharset(Charset charset) {
        this.charsetDefault = charset;
    }

    /**
     * 使用反射机制获取进程的pid
     *
     * @param pro 进程
     * @return
     * @throws IllegalArgumentException IllegalArgumentException
     * @throws IllegalAccessException   IllegalAccessException
     * @throws NoSuchFieldException     NoSuchFieldException
     * @throws SecurityException        SecurityException
     */
    public int getPid(Process pro) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        Class<?> cProcessImpl = pro.getClass();
        Field fPid = cProcessImpl.getDeclaredField("pid");
        if (!fPid.isAccessible()) {
            fPid.setAccessible(true);
        }
        return fPid.getInt(pro);
    }

    /**
     * @param cmd         cmd
     * @param baseDir     baseDir
     * @param regex       regex
     * @param replacement replacement
     * @return
     * @throws IOException IOException
     */
    public String find(String cmd, String baseDir, String regex, String replacement) throws IOException {
        return find(cmd, baseDir, Pattern.compile(regex), replacement);
    }

    /**
     * @param cmd         cmd
     * @param baseDir     baseDir
     * @param pattern     pattern
     * @param replacement replacement
     * @return
     * @throws IOException IOException
     */
    public String find(String cmd, String baseDir, Pattern pattern, String replacement) throws IOException {
        Process pro = getProcess(cmd, baseDir);
        try {
            InputStream in = execByProcess(pro);
            return IOUtils.find(in, pattern, replacement, charsetDefault);
        } finally {
            destroy(pro);
            pro = null;
        }
    }

    /**
     * @param cmd         cmd
     * @param baseDir     baseDir
     * @param regex       regex
     * @param replacement replacement
     * @return
     * @throws IOException IOException
     */
    public Long findLong(String cmd, String baseDir, String regex, String replacement) throws IOException {
        return findLong(cmd, baseDir, Pattern.compile(regex), replacement);
    }

    /**
     * @param cmd         cmd
     * @param baseDir     baseDir
     * @param pattern     pattern
     * @param replacement replacement
     * @return
     * @throws IOException IOException
     */
    public Long findLong(String cmd, String baseDir, Pattern pattern, String replacement) throws IOException {
        Process pro = getProcess(cmd, baseDir);
        try {
            InputStream in = execByProcess(pro);
            return IOUtils.findLong(in, pattern, replacement, charsetDefault);
        } finally {
            destroy(pro);
            pro = null;
        }
    }

    /**
     * @param cmd         cmd
     * @param baseDir     baseDir
     * @param regex       regex
     * @param replacement replacement
     * @return
     * @throws IOException IOException
     */
    public List<String> findAll(String cmd, String baseDir, String regex, String replacement) throws IOException {
        return findAll(cmd, baseDir, Pattern.compile(regex), replacement);
    }

    /**
     * @param cmd         cmd
     * @param baseDir     baseDir
     * @param pattern     pattern
     * @param replacement replacement
     * @return
     * @throws IOException IOException
     */
    public List<String> findAll(String cmd, String baseDir, Pattern pattern, String replacement) throws IOException {
        Process pro = getProcess(cmd, baseDir);
        try {
            InputStream in = execByProcess(pro);
            return IOUtils.findAll(in, pattern, replacement, charsetDefault);
        } finally {
            destroy(pro);
            pro = null;
        }
    }

    /**
     * 执行cmd命令获取返回的所有行
     * <p>
     * 要小心
     * 所有读取内容的方法都有可能导致卡死，原因是某些shell命令读取inputSteam时判断不了何时读取结束
     * </p>
     *
     * @param cmd     cmd
     * @param baseDir baseDir
     * @return
     * @throws IOException IOException
     */
    public List<String> readLines(String cmd, String baseDir) throws IOException {
        Process pro = getProcess(cmd, baseDir);
        try {
            InputStream in = execByProcess(pro);
            return IOUtils.readLines(in, charsetDefault);
        } finally {
            destroy(pro);
            pro = null;
        }
    }

    /**
     * 执行cmd命令获取返回的所有行,每一行的内容实时由consumer接收
     * <p>
     * 要小心
     * 所有读取内容的方法都有可能导致卡死，原因是某些shell命令读取inputSteam时判断不了何时读取结束
     * </p>
     *
     * @param cmd      cmd
     * @param baseDir  baseDir
     * @param consumer 消费行的内容，尽量不要在内部做耗时操作
     * @throws IOException IOException
     */
    public void readLines(String cmd, String baseDir, java.util.function.Consumer<String> consumer) throws IOException {
        Process pro = getProcess(cmd, baseDir);
        try {
            InputStream in = execByProcess(pro);
            if (consumer != null) {
                IOUtils.readLines(in, consumer, charsetDefault);
            } else {
                IOUtils.readLines(in, charsetDefault);
            }

        } finally {
            destroy(pro);
            pro = null;
        }
    }

    /**
     * 执行cmd命令获取返回的所有行,每一行的内容实时由consumer接收
     * <p>
     * 要小心
     * 所有读取内容的方法都有可能导致卡死，原因是某些shell命令读取inputSteam时判断不了何时读取结束
     * </p>
     *
     * @param cmd      cmd
     * @param baseDir  baseDir
     * @param consumer 消费行的内容，尽量不要在内部做耗时操作
     * @throws Throwable Throwable
     */
    public void readLinesWithException(String cmd, String baseDir, ConsumerE<String> consumer) throws Throwable {
        Process pro = getProcess(cmd, baseDir);
        try {
            InputStream in = execByProcess(pro);
            if (consumer != null) {
                IOUtils.readLinesWithException(in, consumer, charsetDefault);
            } else {
                IOUtils.readLines(in, charsetDefault);
            }

        } finally {
            destroy(pro);
            pro = null;
        }
    }

    /**
     * 执行cmd命令获取返回
     *
     * @param cmd          cmd
     * @param baseDir      baseDir
     * @param lineSparator 行分隔符
     * @return
     * @throws IOException IOException
     */
    public String readText(String cmd, String baseDir, String lineSparator) throws IOException {
        Process pro = getProcess(cmd, baseDir);
        try {
            InputStream in = execByProcess(pro);
            return IOUtils.readText(in, lineSparator, charsetDefault);
        } finally {
            destroy(pro);
            pro = null;
        }
    }

    /**
     * 执行命令并返回IO流
     * <p>
     * 该方法未关闭process,请慎用
     * </p>
     *
     * @param cmd     cmd
     * @param baseDir baseDir
     * @return
     * @throws IOException IOException
     */
    @Deprecated
    public InputStream execForStream(String cmd, String baseDir) throws IOException {
        Process pro = getProcess(cmd, baseDir);
        return execByProcess(pro);
    }

    /**
     * 执行cmd命令(防进程挂起),只关心成功与否,不关心返回
     *
     * @param cmd     cmd
     * @param baseDir baseDir
     * @return
     */
    public boolean exec(String cmd, String baseDir) {
        try {
            return execWithException(cmd, baseDir);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 执行cmd命令(防进程挂起),只关心成功与否,不关心返回
     *
     * @param cmd     cmd
     * @param baseDir baseDir
     * @return
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public boolean execWithException(String cmd, String baseDir) throws IOException, InterruptedException {
        Process shellPro = getProcess(cmd, baseDir);
        try {
            pre(shellPro);        //关闭输出流,子线程读取错误流
            readOutput(shellPro);    //子线程读取输入流
            int exitValue = shellPro.waitFor();
            return exitValue == 0;
        } finally {
            destroy(shellPro);
            shellPro = null;
        }
    }

    /**
     * 执行命令并得到返回流
     *
     * @param pro pro
     * @return
     */
    public InputStream execByProcess(Process pro) {
        pre(pro);
        return pro.getInputStream();
    }

    public abstract Process getProcess(String cmd, String baseDir) throws IOException;

    /*===内部方法=======*/
    protected void destroy(Process process) {
        if (process != null) {
            process.destroy();
        }
    }
}