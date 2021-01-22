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

package cc.jweb.boot.controller;


import cn.hutool.core.util.StrUtil;
import cc.jweb.boot.annotation.Column;
import cc.jweb.boot.common.utils.DateUtils;
import cc.jweb.boot.utils.gson.GsonUtils;
import cc.jweb.boot.utils.lang.StringUtils;
import com.jfinal.core.NotAction;
import io.jboot.web.controller.JbootController;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 服务层注解实现
 *
 * @author LinCH
 */
public abstract class BaseController extends JbootController {
    /**
     * 分页模式(start 行模式 page 页模式)
     */
    private static String PAGING_MODE = "start";
    /**
     * 查询的start开始标识
     */
    private static String PAGING_START_KEY = "start";
    /**
     * 查询的limit开始标识
     */
    private static String PAGING_LIMIT_KEY = "limit";

    @NotAction
    public static boolean isAjax(HttpServletRequest request) {
        if ((request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").contains("XMLHttpRequest"))) {
            if (request.getHeader("accept") == null || request.getHeader("accept").contains("application/json")) {
                return true;
            }
        }
        if (StringUtils.isNotBlank(request.getParameter("_dataType")) && request.getParameter("_dataType").equals("json")) {
            return true;
        }
        return false;
    }

    @NotAction
    @SuppressWarnings("unchecked")
    public static <T> T[] unique(Class<T> cls, T[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }
        Set<T> set = new HashSet<T>();
        for (int i = 0, ln = arr.length; i < ln; i++) {
            set.add(arr[i]);
        }
        return set.toArray((T[]) Array.newInstance(cls, set.size()));
    }

    @NotAction
    public boolean isAjax() {
        return isAjax(getRequest());
    }

    /**
     * 获得请求体
     *
     * @return
     */
    @NotAction
    public String getRequestBody() {
        try {
            return IOUtils.toString(getRequest().getInputStream(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得请求体的map对象
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @NotAction
    public Map<String, Object> getRequestBodyForMap() {
        String str = getRequestBody();
        if (str == null) {
            return null;
        } else {
            return GsonUtils.get().toMap(str);
        }
    }

    /**
     * 获得请求中的参数
     *
     * @return
     */
    @NotAction
    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        for (Map.Entry<String, String[]> entry : getRequest().getParameterMap().entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue().length == 1) {
                    params.put(entry.getKey(), entry.getValue()[0]);
                } else if (entry.getValue().length > 1) {
                    params.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return params;
    }

    /**
     * 获取layui分页参数
     *
     * @return
     */
    @NotAction
    public Map<String, Object> getPageParamsPlus() {
        Map<String, Object> pageParams = getPageParams();
        Integer page = getParaToInt("page");
        Integer size = getParaToInt("size");
        Integer start = getParaToInt("start");
        Integer limit = getParaToInt("limit");
        limit = limit != null ? limit : size != null ? size : 10;
        start = start != null ? start : page != null ? (page - 1) * limit : 0;
        pageParams.put("start", start);
        pageParams.put("limit", limit);
        return pageParams;
    }

    /**
     * 获得带分页的请求参数
     *
     * @return
     */
    @NotAction
    public Map<String, Object> getPageParams() {
        Map<String, Object> params = getParams();
        /**
         * 获取分页模式
         * rows 代表已传递参数为 start limit
         * page 代表传递的参数为 pageNo limit
         */
        if ("rows".equals(PAGING_MODE)) {
            params.put("start", MapUtils.getInteger(params, PAGING_START_KEY, 0));
            params.put("limit", MapUtils.getInteger(params, PAGING_LIMIT_KEY, 10));
        } else if ("page".equals(PAGING_MODE)) {
            /* 执行转换 (page - 1) * pageRows + 1, page * pageRows */
            int pageNo = MapUtils.getInteger(params, PAGING_START_KEY, 1);
            int limit = MapUtils.getInteger(params, PAGING_LIMIT_KEY, 10);
            params.put("start", (pageNo - 1) * limit);
            params.put("limit", limit);
        }
        return params;
    }

    @NotAction
    public String getIpAddress() {
        return getIpAddress(getRequest());
    }

    @NotAction
    public String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
            // = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

//    public <T> T getModel(Class<T> modelClass, String modelName) {
//        T model = super.getModel(modelClass, modelName);
//        T columnModel = getColumnModel(modelClass);
//        return model;
//    }


    /**
     * 获得列模型
     *
     * @param modelClass
     * @return
     */
    @SuppressWarnings("unchecked")
    @NotAction
    public <T> T getColumnModel(Class<T> modelClass) {
        /* 实例化类 */
        Object temp = null;
        try {
            temp = modelClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        /* 获得类中包含@Column的列 */
        Field[] fields = modelClass.getDeclaredFields();
        Method setMethod = null;
        try {
            setMethod = modelClass.getMethod("set", String.class, Object.class);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
        for (Field f : fields) {
            Column col = f.getAnnotation(Column.class);
            if (col != null) {
                String field_name = StrUtil.isBlank(col.field()) ? f.getName() : col.field();
                String field_type = f.getType().getSimpleName().toLowerCase();
                /* 若列在请求中不存在就不执行设值方法 */
                if (getRequest().getParameter(field_name) == null) {
                    continue;
                }
                /* int */
                if ("int".equals(field_type) || "integer".equals(field_type)) {
                    try {
                        setMethod.invoke(temp, field_name, getParaToInt(field_name));
                        continue;
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                if ("string".equals(field_type)) {
                    try {
                        setMethod.invoke(temp, field_name, getPara(field_name));
                        continue;
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                if ("double".equals(field_type)) {
                    try {
                        setMethod.invoke(temp, field_name, Double.parseDouble(getPara(field_name)));
                        continue;
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                if ("float".equals(field_type)) {
                    try {
                        setMethod.invoke(temp, field_name, Float.parseFloat(getPara(field_name)));
                        continue;
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                if ("boolean".equals(field_type)) {
                    try {
                        setMethod.invoke(temp, field_name, getParaToBoolean(field_name));
                        continue;
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                if ("long".equals(field_type)) {
                    try {
                        setMethod.invoke(temp, field_name, getParaToLong(field_name));
                        continue;
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                if ("date".equals(field_type)) {
                    try {
                        /* 判断参数 */
                        if (StringUtils.isNotBlank(getPara(field_name))) {
                            String dateString = getPara(field_name);
                            try {
                                dateString = DateTime.parse(dateString, DateTimeFormat.forPattern(col.dateFormat())).toString(col.dateFormat());
                            } catch (Exception exception) {
                                try {
                                    dateString = new SimpleDateFormat(col.dateFormat()).format(DateUtils.commonDateParse(dateString));
                                } catch (Exception e) {
                                    throw new IllegalArgumentException(e);
                                }
                            }
                            setMethod.invoke(temp, field_name, dateString);
                        }
                        continue;
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return temp == null ? null : (T) temp;
    }

    /**
     * 判断浏览器类型
     *
     * @return
     */
    @NotAction
    public String getBrowserType() {
        String userAgent = getRequest().getHeader("User-Agent").toLowerCase();
        String type = null;
        if (userAgent.indexOf("micromessenger") > 0) {
            return "weixin";
        } else if (userAgent.indexOf("alipayclient") > 0) {
            return "alipay";
        }
        return type;
    }
	/*public static Object[] unique(Object [] arr){
        //实例化一个set集合
        Set<Object> set = new HashSet<Object>();
        //遍历数组并存入集合,如果元素已存在则不会重复存入
        for (int i = 0; i < arr.length; i++) {
            set.add(arr[i]);
        }
        //返回Set集合的数组形式
        return set.toArray();
    }
	public Object[] getParaValuesForUnique(String name){
		String[] arr = getParaValues(name);
		if(arr!=null){
			return unique(arr);
		}
		return null;
	}*/

}