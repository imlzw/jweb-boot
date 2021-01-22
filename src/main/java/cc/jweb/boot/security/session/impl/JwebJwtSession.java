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

package cc.jweb.boot.security.session.impl;

import cc.jweb.boot.security.config.JwebJwtConfig;
import cc.jweb.boot.security.config.JwebSecurityConfig;
import cc.jweb.boot.security.session.JwebSecuritySession;
import cc.jweb.boot.security.session.account.JwebSecurityAccount;
import cc.jweb.boot.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * jwt session 实现json web token 的 session
 */
public class JwebJwtSession extends JwebSecuritySession {

    private final static String ACCOUNT_SESSION_PRE_KEY = "JWEB_A_";
    private final static String ACCOUNT_SESSION_EXT_PRE_KEY = "JWEB_A_E_";
    private final static String ACCOUNT_SESSION_UID_KEY = ACCOUNT_SESSION_PRE_KEY + "UID";
    private final static String ACCOUNT_SESSION_UNAME_KEY = ACCOUNT_SESSION_PRE_KEY + "UNAME";
    private boolean isModify = false;

    private Map<String, Object> sessionData = new HashMap<>(16);
    private JwebSecurityAccount account = null;
    private String token = null;

    public JwebJwtSession(HttpServletRequest request, HttpServletResponse response, JwebSecurityConfig config) {
        super(request, response, config);
        initSessionData(request, config);
    }

    private void initSessionData(HttpServletRequest request, JwebSecurityConfig config) {
        String jwtStorePosition = config.getJwt().getStorePosition();
        String jwtKeyName = config.getJwt().getStoreKey();
        String secret = config.getJwt().getSecret();
        if (secret == null) {
            throw new JwtException("jwt secret can't be null!");
        }
        String token = null;
        if (JwebJwtConfig.POSITION_HEADER.equalsIgnoreCase(jwtStorePosition)) {
            token = request.getHeader(jwtKeyName);
        } else {
//            String cookie = request.getHeader("cookie");
            Cookie cookies[] = request.getCookies();
            if (cookies != null) {
                for (Cookie ck : cookies) {
                    if (ck.getName().equalsIgnoreCase(jwtKeyName)) {
                        token = ck.getValue();
                    }
                }
            }
        }
        if (token != null) {
            Map map = JwtUtils.parseTokenBody(token, secret);
            if (map != null) {
                this.sessionData.putAll(map);
            }
        }
        this.token = token;
    }

    @Override
    public JwebSecurityAccount getAccount() {
        if (account == null) {
            String uid = (String) sessionData.remove(ACCOUNT_SESSION_UID_KEY);
            String uname = (String) sessionData.remove(ACCOUNT_SESSION_UNAME_KEY);
            if (uid != null && uid.trim().length() > 0) {
                account = new JwebSecurityAccount(uid, uname);
                // 加载扩展信息
                Set<String> keys = new HashSet<>(sessionData.keySet());
                for (String key : keys) {
                    if (key.indexOf(ACCOUNT_SESSION_EXT_PRE_KEY) == 0) {
                        account.setExt(key.substring(ACCOUNT_SESSION_EXT_PRE_KEY.length()), (String) sessionData.remove(key));
                    }
                }
                account.setModify(false);
            }
        }
        return account;
    }

    @Override
    public void setAccount(JwebSecurityAccount jwebSecurityAccount) {
        this.account = jwebSecurityAccount;
        isModify = true;
    }

    private String getNewAttrName(String attrName) {
        return "S_" + attrName;
    }

    @Override
    public Object getAttribute(String attrName) {
        return sessionData.get(getNewAttrName(attrName));
    }


    @Override
    public void setAttribute(String attrName, Object value) {
        if (value != null && value instanceof String) {
            sessionData.put(getNewAttrName(attrName), (String) value);
            isModify = true;
        } else {
            // doing nothing
        }
    }

    @Override
    public void removeAttribute(String attrName) {
        sessionData.remove(getNewAttrName(attrName));
        isModify = true;
    }

    @Override
    public void setTimeoutSeconds(int timeoutSeconds) {
        super.setTimeoutSeconds(timeoutSeconds);
        isModify = true;
    }

    @Override
    public boolean isAuthentication() {
        return getAccount() != null;
    }

    @Override
    public boolean hasRole(String role) {
        return false;
    }

    @Override
    public boolean[] hasRoles(String... roles) {
        return new boolean[0];
    }

    @Override
    public boolean hasAllRoles(String... roles) {
        return false;
    }

    @Override
    public boolean isPermitted(String permission) {
        return false;
    }

    @Override
    public boolean[] isPermitted(String... permissions) {
        return new boolean[0];
    }

    @Override
    public boolean isPermittedAll(String... permissions) {
        return false;
    }

    @Override
    public void invalidate() {
        sessionData.clear();
        account = null;
        token = null;
        HttpSession session = this.getRequest().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        isModify = true;
    }

    @Override
    public void postIntercept() {
//        long start = System.currentTimeMillis();
        // session data 是否存在修改？
        if (isModify || (getAccount() != null && getAccount().isModify())) {
            refreshToken();
        } else {
            // 如果sessionData没有修改, 隔一小段时间更新token，避免每个请求刷新token,提高访问性能
            refreshIfNecessary();
        }
//        System.out.println("postIntercept cost time:" + (System.currentTimeMillis() - start));
    }

    /**
     * 刷新token
     */
    private void refreshToken() {
        // 将account放进sessionData中
        if (getAccount() != null) {
            String uid = getAccount().getUid();
            String uname = getAccount().getUname();
            if (uid != null) {
                sessionData.put(ACCOUNT_SESSION_UID_KEY, uid);
                sessionData.put(ACCOUNT_SESSION_UNAME_KEY, uname);
            }
            Map<String, String> extInfo = getAccount().getExtInfo();
            if (extInfo != null && !extInfo.isEmpty()) {
                for (String key : extInfo.keySet()) {
                    sessionData.put(ACCOUNT_SESSION_EXT_PRE_KEY + key, extInfo.get(key));
                }
            }
        }

        // 将sessionData更新到token中
        String secret = getJwebSecurityConfig().getJwt().getSecret();
        String jwtToken = null;
        if (!sessionData.isEmpty()) {
            jwtToken = JwtUtils.createJwtToken(sessionData, secret, this.getTimeoutSeconds());
        } else {
            // delete token header or cookie
        }
        String storePosition = getJwebSecurityConfig().getJwt().getStorePosition();
        String storeKey = getJwebSecurityConfig().getJwt().getStoreKey();
        if (JwebJwtConfig.POSITION_HEADER.equalsIgnoreCase(storePosition)) {
            JwtUtils.responseJwt2Header(getResponse(), storeKey, jwtToken);
        } else {
            JwtUtils.responseJwt2Cookie(getResponse(), storeKey, jwtToken);
        }
    }

    /**
     * 有需要时，刷新token
     * 如果sessionData没有修改, 隔一小段时间更新token，避免每个请求刷新token,提高访问性能
     */
    private void refreshIfNecessary() {
        // 当sessionData为空时
        if (sessionData.isEmpty()) {
            // token 过期或者token无效导致的sessionData为空。此时，刷新token,清理token
            if (token != null) {
                refreshToken();
            } else {
                // token 为空 ，sessionData无数据为正常现象，不做处理
                return;
            }
        }
        // Jwt token 的发布时间
        Integer issueAtSeconds = (Integer) sessionData.get(Claims.ISSUED_AT);
        Integer timeoutSeconds = (Integer) sessionData.get(JwtUtils.TIMEOUT_S);
        // 如果token不存在？不刷新
        if (issueAtSeconds == null) {
            return;
        }
        // token永久有效，并且未变更，不刷新
        if (timeoutSeconds <= 0 && this.getTimeoutSeconds() <= 0) {
            return;
        }
        // 已经发布的时间
        long pastSeconds = System.currentTimeMillis() / 1000 - issueAtSeconds;
        // 已经发布的时间 大于有效期的十分之一，重新刷新
        if (pastSeconds > (this.getTimeoutSeconds()) / 10) {
            refreshToken();
        }
    }
}
