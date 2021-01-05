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