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


import cc.jweb.boot.utils.lang.collection.ListUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {


    private static final Pattern PATTERN_EMPTY = Pattern.compile("^[\\u00A0\\s　]*$");    //空值验证(\\u00A0为ASCII值为160的空格)
    private static final Pattern PATTERN_EMPTY_LEFT = Pattern.compile("^[\\u00A0\\s　]+");    //空值验证(\\u00A0为ASCII值为160的空格)
    private static final Pattern PATTERN_EMPTY_RIGHT = Pattern.compile("[\\u00A0\\s　]+$");    //空值验证(\\u00A0为ASCII值为160的空格)

    /**
     * 判断对象是否不为空，包括空格
     *
     * @param o
     * @return
     */
    public static boolean isBlank(Object o) {
        return !isNotBlank(o);
    }


    /**
     * 判断对象是否不为空，包括空格
     *
     * @param o
     * @return
     */
    public static boolean notBlank(Object o) {
        return isNotBlank(o);
    }

    /**
     * 判断对象是否不为空，包括空格
     *
     * @param o
     * @return
     */
    public static boolean isNotBlank(Object o) {
        if (o == null) {
            return false;
        } else {
            return isNotBlank(o.toString());
        }
    }

    /**
     * Java中1个char类型的变量可存储任意编码的1个字符，如1个ASC码和或1个中文字符，
     * 例如：含有3个ASC和含有3个汉字字符的字符串长度是一样的： "1ac".length()==3;  "你好a".length()=3;
     * 但上述两个字符串所占的byte是不一样的，前者是3，后者是5（1个汉字2byte）。
     * 　       * 请编写函数:
     * 　       *     public static String leftStr(String source, int maxByteLen)
     * 从source中取最大maxByteLen个byte的子串。
     * 当最后一个byte恰好为一个汉字的前半个字节时，舍弃此byte。例如：
     * String source="我LRW爱JAVA";
     * leftStr(source,1,-1)=="";
     * leftStr(source,2,-1)=="我";
     * leftStr(source,4,-1)=="我LR";
     * leftStr(source,11,-1)=="我LRW";
     * 当最后一个byte恰好为一个汉字的前半个字节时，补全汉字（多取一个字节）。例如：
     * String source="我LRW爱JAVA";
     * leftStr(source,1,1)=="我";
     * leftStr(source,2,1)=="我";
     * leftStr(source,4,1)=="我LR";
     * leftStr(source,11,1)=="我LRW爱";
     *
     * @param source     原始字符串
     * @param maxByteLen 截取的字节数
     * @param flag       表示处理汉字的方式。1表示遇到半个汉字时补全，-1表示遇到半个汉字时舍弃
     * @return 截取后的字符串
     */
    public static String subString(String source, int maxByteLen, int flag) {
        if (source == null || maxByteLen <= 0) {
            return "";
        }
        byte[] bStr = source.getBytes();
        if (maxByteLen >= bStr.length) return source;
        String cStr = new String(bStr, maxByteLen - 1, 2);
        if (cStr.length() == 1 && source.contains(cStr)) {
            maxByteLen += flag;
        }
        return new String(bStr, 0, maxByteLen);
    }

    /**
     * 截取字符串
     *
     * @param str                       字符串
     * @param maxByteLength             最大字符串长度（一个汉字算两个字节）
     * @param completeChineseCharacters 是否保留完整的汉字（若遇到截取到半个汉字的情况下）
     * @return
     */
    public static String subString(String str, int maxByteLength, boolean completeChineseCharacters) {
        return subString(str, maxByteLength, completeChineseCharacters ? 1 : -1);
    }

    /* 去左空格 */
    public static String leftTrim(String str) {
        if (str == null || str.equals("")) {
            return str;
        } else {
            return str.replaceAll("^[　 ]+", "");
        }
    }

    /* 去右空格 */
    public static String rightTrim(String str) {
        if (str == null || str.equals("")) {
            return str;
        } else {
            return str.replaceAll("[　 ]+$", "");
        }
    }

    public static String getMarkString(String content, int begin, int end, String mark) {

        if (begin >= content.length() || begin < 0) {
            return content;
        }
        if (end >= content.length() || end < 0) {
            return content;
        }
        if (begin >= end) {
            return content;
        }
        String starStr = "";
        for (int i = begin; i < end; i++) {
            starStr = starStr + mark;
        }
        return content.substring(0, begin) + starStr + content.substring(end, content.length());

    }

    /**
     * 对字符串处理:将指定位置到指定位置的字符以星号代替
     *
     * @param content 传入的字符串
     * @param begin   开始位置
     * @param end     结束位置
     * @return
     */
    public static String getMarkString(String content, int begin, int end) {
        return getMarkString(content, begin, end, "*");
    }

    /**
     * 对字符加星号处理：除前面几位和后面几位外，其他的字符以星号代替
     *
     * @param content  传入的字符串
     * @param frontNum 保留前面字符的位数
     * @param endNum   保留后面字符的位数
     * @return 带星号的字符串
     */

    public static String getMarkStringByExclude(String content, int frontNum, int endNum, String mark) {

        if (frontNum >= content.length() || frontNum < 0) {
            return content;
        }
        if (endNum >= content.length() || endNum < 0) {
            return content;
        }
        if (frontNum + endNum >= content.length()) {
            return content;
        }
        String starStr = "";
        for (int i = 0; i < (content.length() - frontNum - endNum); i++) {
            starStr = starStr + mark;
        }
        return content.substring(0, frontNum) + starStr
                + content.substring(content.length() - endNum, content.length());
    }

    public static String getMarkStringByExclude(String content, int frontNum, int endNum) {
        return getMarkStringByExclude(content, frontNum, endNum, "*");
    }

    /**
     * 获得字符串长度(utf-8)
     *
     * @param src 源字符串
     * @return
     * @see #getLength(String, Charset)
     */
    public static int getLength(String src) {
//		src = src.replaceAll("[^\\x00-\\xff]", "**");
//		int length = src.length();
        return getLength(src, StandardCharsets.UTF_8);
    }

    /**
     * 获得字符串长度<br>
     * 实现原理:String.getBytes(charset).length
     *
     * @param src     src
     * @param charset charset
     * @return
     */
    public static int getLength(String src, Charset charset) {
        if (isEmpty(src)) {
            return 0;
        }
        return src.getBytes(charset).length;
    }

    /**
     * 字符串是否为null或者长度为0
     *
     * @param src src
     * @return
     */
    public static boolean isEmpty(String src) {
        if (src == null || src.length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为null获取为空字符串(最多只含制表符 \t ('\u0009'),换行符 \n ('\u000A'),回车符 \r ('\u000D')，换页符 \f ('\u000C')以及半角/全角空格)
     *
     * @param src src
     * @return
     */
    public static boolean isBlank(String src) {
        if (src == null || PATTERN_EMPTY.matcher(src).matches()) {
            return true;
        }
        return false;
    }

    /**
     * 如果字符串为null则返回空字符串
     *
     * @param src src
     * @return
     */
    public static String emptyIfNull(Object src) {
        if (src == null) {
            return "";
        }
        return src.toString();
    }

    /**
     * 去除首尾空格
     * <p>
     * java原生的trim有不完善的地方,无法去除特定的空格
     * 该实现为正则"[\\u00A0\\s　]+"首尾匹配出的空格替换为空字符串
     * </p>
     *
     * @param src src
     * @return
     */
    public static String trim(String src) {
        if (src == null) {
            return null;
        }
        src = PATTERN_EMPTY_LEFT.matcher(src).replaceFirst("");
        return PATTERN_EMPTY_RIGHT.matcher(src).replaceAll("");
    }

    /**
     * 通过StringBuilder连接字符串
     *
     * @param obj  第一个值
     * @param objs 后续的值
     * @return
     */
    public static String concat(Object obj, Object... objs) {
        StringBuilder sb = new StringBuilder();
        sb.append(obj);
        if (objs != null) {
            for (Object item : objs) {
                if (item != null) {
                    sb.append(item);
                }
            }
        }
        return sb.toString();
    }

    /**
     * {@code
     * summary("aaa",5,"..."); => "aa..."
     * summary("aaa",2,"..."); => ".."
     * summary("aaa",7,"..."); => "aaa..."
     * }
     *
     * @param src      原文字
     * @param limit    限制长度
     * @param ellipsis 省略号
     * @return 文字摘要
     */
    public static String summary(String src, int limit, String ellipsis) {
        if (src == null || limit <= 0) {
            return "";
        }
        if (src.length() <= limit) {
            return src;
        }
        ellipsis = StringUtils.emptyIfNull(ellipsis);
        int end = limit - ellipsis.length();
        if (end > 0) {    //加上省略号超长
            return src.substring(0, end) + ellipsis;
        } else if (end == 0) {    //限制数刚好等于省略号长度
            return ellipsis;
        } else {    //限制数小于省略号长度
            return ellipsis.substring(0, limit);
        }
    }

    /**
     * 利用StringBuilder替换起止位置的字符串
     *
     * @param src    src
     * @param start  start
     * @param end    end
     * @param newStr newStr
     * @return
     */
    public static String replace(String src, int start, int end, String newStr) {
        if (isEmpty(src)) {
            return src;
        }
        StringBuilder sb = new StringBuilder(src);
        return sb.replace(start, end, newStr).toString();
    }

    /**
     * 将一个字符串拆分成每一项定长为length(最后一项不定)的字符串列表<br>
     * <p><pre>{@code
     * splitStr("abc", 2)=>["ab","c"]
     * splitStr("abc",4)=>["abc"]
     * }</pre>
     *
     * @param src    src
     * @param length length
     * @return
     */
    public static List<String> splitStr(String src, int length) {
        if (isEmpty(src)) {
            return ListUtils.newArrayList();
        }
        if (length <= 0) {
            return ListUtils.of(src);
        }
        List<String> list = ListUtils.newArrayList();
        int max = src.length();
        for (int start = 0; start < max; ) {
            int end = start + length;
            if (end > max) {
                end = max;
            }
            list.add(src.substring(start, end));
            start = end;
        }
        return list;
    }

    /**
     * 拼接文件路径(避免重复的分隔符)
     *
     * @param filePath   基础路径
     * @param extraPaths 额外路径
     * @return
     */
    public static String concatFilePath(String filePath, Object... extraPaths) {
        return concatFilePathBySeparator(File.separator, filePath, extraPaths);
    }

    /**
     * 拼接文件路径(该方法会在每两个路径中插入fileSeparator, 替换原有的路径分隔符, 并避免重复的分隔符)
     *
     * @param fileSeparator 系统的路径分隔符,win传"\\",linux传"/"
     * @param filePath      基础路径
     * @param extraPaths    额外路径
     * @return
     */
    public static String concatFilePathBySeparator(String fileSeparator, String filePath, Object... extraPaths) {
        StringBuilder sb = new StringBuilder(filePath).append(fileSeparator);

        if (extraPaths != null) {
            for (Object item : extraPaths) {
                sb.append(fileSeparator);
                if (item != null) {

                    sb.append(item);
                }
            }

        }
        if ("\\".equals(fileSeparator)) {    //需要转义符
            return sb.toString().replaceAll("[\\\\/]+", "\\\\");
        }
        return sb.toString().replaceAll("[\\\\/]+", fileSeparator);
    }

    /**
     * 利用StringBuilder倒置字符串
     *
     * @param src src
     * @return
     */
    public static String reverse(String src) {
        if (isEmpty(src)) {
            return src;
        }
        return new StringBuilder(src).reverse().toString();
    }

    /**
     * 下划线转驼峰法
     *
     * @param line       源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰（如果为false则首字母大写）
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line, boolean smallCamel) {
        if (isEmpty(line)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0))
                    : Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }


    /**
     * 首字母大写
     *
     * @param src 源字符串
     * @return
     */
    public static String upperCaseFirst(String src) {
        if (isEmpty(src)) {
            return src;
        }
        char[] ch = src.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 格式化数字,保留decimalPlaces位小数
     *
     * @param num           num
     * @param decimalPlaces 保留小数位数
     * @return
     */
    public static String formatNum(double num, int decimalPlaces) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(num);
    }

    /**
     * 生成随机的uuid
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 两版本号比较判断旧版本号是否小于新版本号
     * <p>
     * 支持任意多节点的版本号比较
     * </p>
     *
     * @param versionCodeOld versionCodeOld
     * @param versionCodeNew versionCodeNew
     * @return
     */
    public static boolean isVersionBefore(String versionCodeOld, String versionCodeNew) {
        if (versionCodeOld == null || versionCodeOld.trim().isEmpty()) {    //不存在旧版本号肯定是要更新的
            return true;
        }
        if (versionCodeNew == null) {    //不存在新版本肯定也升级不了
            return false;
        }
        if (versionCodeOld.equals(versionCodeNew)) {    //两个版本字符串一致,则说明完全不用升级
            return false;
        }

        //逐级比较
        String[] codesOld = versionCodeOld.split("\\.");
        String[] codesNew = versionCodeNew.split("\\.");
        int length_old = codesOld.length;
        int length_new = codesNew.length;
        for (int i = 0; ; i++) {
            if (i >= length_old) {
                if (i >= length_new) {    //新旧版本都到底了还没分出胜负则平局
                    return false;
                } else {    //旧版本到底了，新版本还有下文，则需要升级
                    return true;
                }
            } else if (i >= length_new) {    //旧版本还有下文，新版本到底了，说明旧版本更新(存在这种情况一版说明版本控制有问题)
                return false;
            } else {    //每级版本进行不同的pk，胜者说明对应的是新版本
                long shouldUpdate = Long.parseLong(codesNew[i]) - Long.parseLong(codesOld[i]);
                if (shouldUpdate > 0) {
                    return true;
                } else if (shouldUpdate < 0) {    //旧版本号比新版本号还大，出现这种情况说明版本控制没操作好
                    return false;
                }
            }

        }
    }

    /**
     * 将url字符串转化成java.net.URL对象
     *
     * @param urlStr urlStr
     * @return
     * @throws MalformedURLException MalformedURLException
     */
    public static java.net.URL toURL(String urlStr) throws MalformedURLException {
        if (urlStr == null) {
            return null;
        }
        if (!urlStr.startsWith("http")) {
            urlStr = "http://" + urlStr;
        }
        java.net.URL url = new java.net.URL(urlStr);
        return url;
    }

    /**
     * 字符串转Double
     *
     * @param src src
     * @return
     */
    public static Double toDouble(String src) {
        try {
            return Double.parseDouble(src);
        } catch (Exception ex) {
            //转换失败
        }
        return null;
    }

    /**
     * 字符串转Float
     *
     * @param src src
     * @return
     */
    public static Float toFloat(String src) {
        try {
            return Float.parseFloat(src);
        } catch (Exception ex) {
            //转换失败
        }
        return null;
    }

    /**
     * 字符串转Integer
     *
     * @param src src
     * @return
     */
    public static Integer toInt(String src) {
        try {
            return Integer.parseInt(src);
        } catch (Exception ex) {
            //转换失败
        }
        return null;
    }

    /**
     * 字符串转Long
     *
     * @param src src
     * @return
     */
    public static Long toLong(String src) {
        try {
            return Long.parseLong(src);
        } catch (Exception ex) {
            //转换失败
        }
        return null;
    }

    /**
     * 字符串转Boolean
     * <p>
     * 当字符串为"true"或者"1"时返回true
     * 当字符串为"false"或者"0"时返回flase
     * 其余情况返回null
     * </p>
     *
     * @param src 源字符串
     * @return
     */
    public static Boolean toBoolean(String src) {
        if (src != null) {
            if ("true".equals(src) || "1".equals(src)) {
                return true;
            } else if ("false".equals(src) || "0".equals(src)) {
                return false;
            }
        }

        return null;
    }

    /**
     * 字符串转Boolean
     *
     * @param src          源字符串
     * @param defaultValue 默认值
     * @return
     */
    public static boolean toBoolean(String src, boolean defaultValue) {
        return ObjectUtils.getOrDefault(toBoolean(src), defaultValue);
    }

    /**
     * 字符串转java.util.Date
     * <p>
     * 支持六种格式
     * yyyy-MM-dd HH:mm:ss
     * yyyy-MM-dd HH:mm
     * yyyy-MM-dd
     * HH:mm:ss
     * 13位数字
     * 10位数字(不包含毫秒数，很多api都这么存，为了不超过Integer类型的最大值2147483647)
     * <p>
     * 值得注意的是:
     * 根据函数System.out.println(
     * DateUtils.toString(Integer.MAX_VALUE*1000l, DateUtils.DEFAULT_TEMPLATE_TIME));
     * 计算得出所有用int类型接收10位时间戳的程序都将在2038-01-19 11:14:07后报错,尽量用long型接收
     * </p>
     *
     * @param src src
     * @return
     */
    public static Date toDate(String src) {
        if (isEmpty(src)) {
            return null;
        }
        if (src.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}")) {    //yyyy-MM-dd HH:mm:ss
            return DateUtils.toDate(src, "yyyy-MM-dd HH:mm:ss");
        } else if (src.matches("\\d{4}-\\d{2}-\\d{2}")) {        //yyyy-MM-dd
            return DateUtils.toDate(src, "yyyy-MM-dd");
        } else if (src.matches("\\d{13}")) {        //13位标准时间戳
            return new Date(toLong(src));
        } else if (src.matches("\\d{10}")) {        //10位标准时间戳
            return new Date(toLong(src) * 1000);
        } else if (src.matches("\\d{2}:\\d{2}:\\d{2}")) {    //HH:mm:ss
            return DateUtils.toDate(src, "HH:mm:ss");
        } else if (src.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}")) {    //yyyy-MM-dd HH:mm
            return DateUtils.toDate(src, "yyyy-MM-dd HH:mm");
        }
        return null;
    }

    /**
     * 源字符串换行转list
     * <p>
     * 对应的拆分正则为(\r)?\n
     * </p>
     *
     * @param src src
     * @return
     */
    public static List<String> toLineList(String src) {
        if (isEmpty(src)) {
            return ListUtils.newArrayList();
        }
        return ListUtils.ofList(src, "(\r)?\n");
    }

    /**
     * 汉字转Unicode字符串
     *
     * @param src src
     * @return
     */
    public static String toUnicode(String src) {
        if (src == null) {
            return null;
        }
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);  // 取出每一个字符
            unicode.append("\\u").append(Integer.toHexString(c));// 转换为unicode
        }
        return unicode.toString();
    }

    /**
     * unicode字符串转汉字
     * <p>
     * 通过\\\\u分隔
     * </p>
     *
     * @param unicode unicode
     * @return
     */
    public static String unicode2String(String unicode) {
        if (unicode == null) {
            return null;
        }
        /* 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split("\\\\u");
        String returnStr = "";
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
        }
        return returnStr;
    }

    /**
     * 将字符串a重复times次
     * <p><pre>{@code
     * 	比如stack("0", 3)=>"000"
     * 函数命名参考游戏minecraft创世神插件的函数名
     * }</pre>
     *
     * @param src   src
     * @param times times
     * @return
     */
    public static String stack(String src, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(src);
        }
        return sb.toString();
    }

    //--转义相关

    /**
     * 16进制转2进制
     * <p>
     * 如果源数据以0x开头会先去掉0x在做处理<br>
     * <pre>{@code
     * decimal2Binary("AA");=>"10101010"
     * decimal2Binary("0xAA");=>"10101010"
     * }</pre>
     *
     * @param src src
     * @return
     */
    public static String decimal2Binary(String src) {
        if (src == null) {
            return null;
        }
        if (src.startsWith("0x")) {
            src = src.substring(2, src.length());
        }
        StringBuilder sb = new StringBuilder();
        int length = src.length();
        src = src.toUpperCase();
        for (int i = 0; i < length; i++) {
            switch (src.charAt(i)) {
                case '0':
                    sb.append("0000");
                    break;
                case '1':
                    sb.append("0001");
                    break;
                case '2':
                    sb.append("0010");
                    break;
                case '3':
                    sb.append("0011");
                    break;
                case '4':
                    sb.append("0100");
                    break;
                case '5':
                    sb.append("0101");
                    break;
                case '6':
                    sb.append("0110");
                    break;
                case '7':
                    sb.append("0111");
                    break;
                case '8':
                    sb.append("1000");
                    break;
                case '9':
                    sb.append("1001");
                    break;
                case 'A':
                    sb.append("1010");
                    break;
                case 'B':
                    sb.append("1011");
                    break;
                case 'C':
                    sb.append("1100");
                    break;
                case 'D':
                    sb.append("1101");
                    break;
                case 'E':
                    sb.append("1110");
                    break;
                case 'F':
                    sb.append("1111");
                    break;
                default:
                    throw new RuntimeException("含有非法字符:" + src.charAt(i) + ",位置:" + i);
            }
        }
        return sb.toString();
    }

    /**
     * 转义xml
     *
     * <p>
     * 外层嵌套CDATA法
     * </p>
     *
     * @param src src
     * @return
     */
    public static String escapeXmlByCDATA(String src) {
        src = emptyIfNull(src);
        return concat("<![CDATA[ ", src, " ]]>");
    }

    /**
     * 转义xml
     *
     * <p>
     * 替换法:
     * <pre>{@code
     * &->&amp;
     * <->&lt;
     * >->&gt;
     * '->&apos;
     * "->&quot;
     * }</pre>
     *
     * @param src src
     * @return
     */
    public static String escapeXmlByReplace(String src) {
        if (isEmpty(src)) {
            return src;
        }
        return src.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;");
    }

}
