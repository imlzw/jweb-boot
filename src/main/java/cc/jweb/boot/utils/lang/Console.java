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

import cc.jweb.boot.utils.Utils;
import cc.jweb.boot.utils.lang.collection.ListUtils;
import cc.jweb.boot.utils.lang.exception.ExceptionUtils;
import cc.jweb.boot.utils.lang.exception.model.JsonSyntaxException;

import java.util.List;


/**
 * 控制台输出辅助类
 * <p>
 * 目的是为了能在项目后期能方便地定位控制台输出代码的位置及控制其输出
 * </p>
 *
 * @author ag777
 * @version last modify at 2020年05月20日
 */
public class Console {

    private static boolean devMode = true;
    private static boolean showSourceMethod = false;

    public static boolean isDevMode() {
        return devMode;
    }

    /**
     * 控制是否打印日志, 默认为true
     *
     * @param devMode devMode
     */
    public static void setDevMode(boolean devMode) {
        Console.devMode = devMode;
    }

    /**
     * 控制是否在输出时显示调用源方法,默认为false
     *
     * @param showSourceMethod showSourceMethod
     */
    public static void showSourceMethod(boolean showSourceMethod) {
        Console.showSourceMethod = showSourceMethod;
    }

    public static boolean showSourceMethod() {
        return showSourceMethod;
    }

    /**
     * 控制台打印格式化信息
     *
     * @param obj obj
     * @return
     */
    public static String prettyLog(Object obj) {
        if (isDevMode()) {
            String msg = getMethod();

            if (obj != null && obj instanceof String) {
                msg += (String) obj;
            } else {
                msg += toJson(obj, true);
            }

            System.out.println(msg);
            return msg;
        }
        return null;
    }

    /**
     * 控制台打印信息
     *
     * @param obj obj
     * @return
     */
    public static String log(Object obj) {
        if (isDevMode()) {
            String msg = getMethod();

            if (obj != null && obj instanceof String) {
                msg += (String) obj;
            } else {
                msg += toJson(obj, false);
            }

            System.out.println(msg);
            return msg;
        }
        return null;
    }

    /**
     * 将传入参数转为list并进行格式化输出
     *
     * @param objs objs
     * @return
     */
    public static String prettyLog(Object obj, Object... objs) {
        if (isDevMode()) {
            if (objs == null) {
                return log(obj);
            }
            String msg = getMethod();
            List<Object> list = ListUtils.of(obj);
            for (Object item : objs) {
                list.add(item);
            }
            msg += toJson(list, true);
            System.out.println(msg);
            return msg;
        }
        return null;
    }

    /**
     * 将传入参数转为list并进行输出
     *
     * @param objs objs
     * @return
     */
    public static String log(Object obj, Object... objs) {
        if (isDevMode()) {
            if (objs == null) {
                return log(obj);
            }
            String msg = getMethod();
            List<Object> list = ListUtils.of(obj);
            for (Object item : objs) {
                list.add(item);
            }
            msg += toJson(list, false);
            System.out.println(msg);
            return msg;
        }
        return null;
    }

    public static void err(String msg) {
        System.err.println(getMethod() + msg);
    }

    /**
     * 控制台打印异常信息
     * @param throwable throwable
     * @param helper helper
     * @return
     */
//	public static String err(Throwable throwable, ExceptionHelper helper) {
//		if(isDevMode()) {
//			String errMsg = helper.getErrMsg(throwable);
//			System.err.println(errMsg);
//			return errMsg;
//		}
//		return null;
//	}

    /**
     * 打印错误栈信息(效果差不多等于)
     *
     * @param throwable throwable
     */
    public static void err(Throwable throwable) {
        String msg = ExceptionUtils.getErrMsg(throwable);
        System.err.println(msg);
    }

    /*=================工具方法====================*/

    /**
     * 获取调用该方法的信息
     *
     * @return
     */
    private static String getMethod() {
        if (!showSourceMethod) {
            return "";
        }

        StackTraceElement[] stacks = (new Throwable()).getStackTrace();
        if (stacks.length > 0) {
            StackTraceElement stack = stacks[stacks.length - 1];
            return new StringBuilder()
                    .append(stack.getClassName())
                    .append('【').append(stack.getMethodName()).append('】')
                    .append(':').append(System.lineSeparator()).toString();
        } else {
            return "";
        }


    }

    /**
     * 统一json转化
     * <p>
     * 先转json后格式化，效率会低一些，建议只在调试的时候用
     * </p>
     *
     * @param obj obj
     * @return
     */
    private static String toJson(Object obj, boolean formatMode) {
        String json = Utils.jsonUtils().toJson(obj);
        if (formatMode) {    //需要被格式化
            try {
                json = Utils.jsonUtils().prettyFormat(json);
            } catch (JsonSyntaxException e) {
            }
        }
        return json;
    }

}