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

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ResponseUtils {

    /**
     * 依据浏览器判断编码规则
     */
    public static String getDownloadFileNameHeader(HttpServletRequest request, String fileName) {
        String userAgent = request.getHeader("User-Agent");
        try {
            String encodedFileName = URLEncoder.encode(fileName, "UTF8");
            // 如果没有UA，则默认使用IE的方式进行编码
            if (userAgent == null) {
                return "filename=\"" + encodedFileName + "\"";
            }

            userAgent = userAgent.toLowerCase();
            // IE浏览器，只能采用URLEncoder编码
            if (userAgent.indexOf("msie") != -1) {
                return "filename=\"" + encodedFileName + "\"";
            }

            // Opera浏览器只能采用filename*
            if (userAgent.indexOf("opera") != -1) {
                return "filename*=UTF-8''" + encodedFileName;
            }

            // Safari浏览器，只能采用ISO编码的中文输出,Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
            if (userAgent.indexOf("safari") != -1 || userAgent.indexOf("applewebkit") != -1 || userAgent.indexOf("chrome") != -1) {
                return "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
            }

            // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
            if (userAgent.indexOf("mozilla") != -1) {
                return "filename*=UTF-8''" + encodedFileName;
            }

            return  "attachment; filename=\"" + encodedFileName + "\"";
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
