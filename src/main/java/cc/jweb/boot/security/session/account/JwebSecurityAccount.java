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

package cc.jweb.boot.security.session.account;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全账户信息,存储账户基本信息
 */
public class JwebSecurityAccount {
    private String uid;
    private String uname;
    private Map<String, String> extInfo;
    private boolean isModify = false;

    public JwebSecurityAccount(String uid, String uname) {
        this.uid = uid;
        this.uname = uname;
        this.extInfo = new HashMap<>(16);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
        isModify = true;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
        isModify = true;
    }

    public String toString() {
        return "uid = " + uid + " , uname = " + uname + " , extInfo = " + extInfo;
    }

    // 设置/更新 扩展信息
    public String setExt(String key, String value) {
        isModify = true;
        return this.extInfo.put(key, value);
    }

    // 移除扩展信息
    public String removeExt(String key) {
        isModify = true;
        return this.extInfo.remove(key);
    }

    public Map<String, String> getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(Map<String, String> extInfo) {
        isModify = true;
        this.extInfo = extInfo;
    }

    public boolean isModify() {
        return isModify;
    }

    public void setModify(boolean isModify) {
        this.isModify = isModify;
    }
}
