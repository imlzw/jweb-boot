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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 执行Cmd命令的工具类
 *
 * @author ag777
 * @version create on 2018年07月04日,last modify at 2018年07月04日
 */
public class CmdUtils extends AbstractCmdUtils {

    public static CmdUtils mInstance;

    public CmdUtils() {
        super();
    }

    /**
     * windows系统请设置编码为GBK
     *
     * @param charset charset
     */
    public CmdUtils(Charset charset) {
        super(charset);
    }

    public static CmdUtils getInstance() {
        if (mInstance == null) {
            synchronized (CmdUtils.class) {
                if (mInstance == null) {
                    mInstance = new CmdUtils();
                }
            }
        }
        return mInstance;
    }

    @Override
    public Process getProcess(String cmd, String baseDir) throws IOException {
        Process pro = null;
        if (baseDir == null) {
            pro = Runtime.getRuntime().exec(cmd);
        } else {
            pro = Runtime.getRuntime().exec(cmd, null, new File(baseDir));// 执行删除默认路由命令
        }
        return pro;
    }

}