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

package cc.jweb.boot.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    public static final String TIMEOUT_S = "tt";     //超时时长(秒）
    public static final String EXPIRE_S = "exp";     //过期时间(秒）
    public static final String ISSUEAT_S = "iat";      //发行时间(秒）

    public static final Map EMPTY_MAP = new HashMap(0);

    public static SecretKey generalSecretKey(String secret) {
        byte[] encodedKey = DatatypeConverter.parseBase64Binary(secret);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    /**
     * 解析 JWT Token 内容
     *
     * @param token 加密的 token
     * @return 返回 JWT 内容 的 MAP 数据
     */
    public static Map parseTokenBody(String token, String secret) {
        SecretKey secretKey = generalSecretKey(secret);
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token).getBody();

        return new HashMap(claims);
    }

    public static String createJwtToken(Map payload, String secret, long timeoutSeconds) {
        SecretKey secretKey = generalSecretKey(secret);
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        payload.put(TIMEOUT_S, timeoutSeconds);
        JwtBuilder builder = Jwts.builder()
                .setClaims(payload)
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey);

        if (timeoutSeconds > 0) {
            long expMillis = nowMillis + timeoutSeconds * 1000;
            builder.setExpiration(new Date(expMillis));
        }
        return builder.compact();
    }


    public static void main(String[] args) {
        HashMap payload = new HashMap();
        payload.put("tttt", 122312323);
        payload.put("tttt2", 122312323);
        String token = createJwtToken(payload, "MTIz", 120);
        System.out.println(token);
        Map playload = parseTokenBody(token, "MTIz");
        System.out.println(playload);
    }

    /**
     * 输出 jwt 内容到客户端(Header)
     *
     * @param response
     * @param headerName
     * @param token
     */
    public static void responseJwt2Header(HttpServletResponse response, String headerName, String token) {
        response.addHeader(headerName, token);
    }

    /**
     * 输出 jwt 内容到客户端(Cookie)
     *
     * @param response
     * @param cookieName
     * @param token
     */
    public static void responseJwt2Cookie(HttpServletResponse response, String cookieName, String token) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setPath("/");
        if (token == null) {
            cookie.setMaxAge(0);
        }
        response.addCookie(cookie);
    }


}
