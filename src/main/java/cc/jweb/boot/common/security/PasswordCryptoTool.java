/*
 * Copyright  (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
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

package cc.jweb.boot.common.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 密码加密工具类
 *
 * Created by 志伟 on 2016/10/13.
 */
public class PasswordCryptoTool {

    /**
     * 加密密码
     *
     * @param plainPw
     * @return
     */
    public static String encryptPassword(String plainPw){
        //FIXME 可以在hash前，对password预先处理，使破解难度加大
        return BCrypt.hashpw(plainPw,BCrypt.gensalt());
    }

    /**
     * 密码检查
     * @param plainPw
     * @param encryptPw
     * @return
     */
    public static boolean checkPassword(String plainPw,String encryptPw){
        //FIXME 可以在hash前，对password预先处理，使破解难度加大
        return BCrypt.checkpw(plainPw,encryptPw);
    }

    public static void main(String[] args) {
        System.out.println(encryptPassword("admin"));
    }

}
