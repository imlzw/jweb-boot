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

package cc.jweb.boot.common.lang;


import cc.jweb.boot.utils.lang.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class Error extends LinkedHashMap<String, Object> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6995040057437913540L;

    public Error(String message) {
        this.setMessage(message);
    }

    public Error(String code, String message) {
        this.setCode(code);
        this.setMessage(message);
    }

    public Error(String code, String message, Integer status) {
        this.setCode(code);
        this.setMessage(message);
        this.setStatus(status);
    }

    public Error(String code, String message, String stack) {
        this.setCode(code);
        this.setMessage(message);
        this.setStack(stack);
    }

    public Error(String code, String message, Integer status, String stack) {
        this.setCode(code);
        this.setMessage(message);
        this.setStatus(status);
        this.setStack(stack);
    }

    public Error(String code, String message, String stack, Integer status) {
        this.setCode(code);
        this.setMessage(message);
        this.setStack(stack);
        this.setStatus(status);
    }

    public String getMessage() {
        return StringUtils.isBlank(this.get("message")) ? "" : this.get("message").toString();
    }

    public void setMessage(String message) {
        if (StringUtils.isNotBlank(message)) {
            this.put("message", message);
        }
    }

    public void setCode(String code) {
        if (StringUtils.isNotBlank(code)) {
            this.put("code", code);
        }
    }

    public void setStack(String stack) {
        if (StringUtils.isNotBlank(stack)) {
            this.put("stack", stack);
        }
    }

    public void setStatus(Integer status) {
        if (status != null) {
            this.put("status", status);
        }
    }
}