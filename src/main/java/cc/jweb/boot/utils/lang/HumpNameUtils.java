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

package cc.jweb.boot.utils.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 驼峰命名转换
 *
 * @author 47475
 */
public class HumpNameUtils {
    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 下划线转驼峰 ,大写自动转小写<br />
     * 例如：USER_NAME->userName<br />
     * USERNAME->userName<br />
     * USER_NAME->userName<br />
     * fParentNoLeader->fParentNoLeader
     */
    public static String lineToHump(String str, boolean firstUpperCase) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        if (firstUpperCase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }
        return sb.toString();
    }

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 驼峰转下划线,大写自动转小写<br />
     * 例如： USER_NAME->user_name USERNAME->userName<br />
     * f_parent_no_leader->f_parent_no_leader
     */
    public static String humpToLine(String str) {
        /** 简单写法，效率不高 */
//		return str.replaceAll("[A-Z]", "_$0").toLowerCase();
        /** 正则表达式高效写法 */
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
        /** 字符串比较写法 */
        /*
         * if (str == null || "".equals(str.trim())) { return ""; } int len =
         * str.length(); StringBuilder sb = new StringBuilder(len); for (int i = 0; i <
         * len; i++) { char c = str.charAt(i); if (Character.isUpperCase(c)) {
         * sb.append('_'); sb.append(Character.toLowerCase(c)); } else { sb.append(c); }
         * } return sb.toString();
         */
    }

    public static void main(String[] args) {
        System.out.println(lineToHump("user_name", true));// userName
        System.out.println(humpToLine("UserName"));// user_name
    }
}