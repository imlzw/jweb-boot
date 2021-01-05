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
