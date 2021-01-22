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

package cc.jweb.boot.interceptor;//package com.ndasec.jfinal.interceptor;
//
//import com.jfinal.aop.Interceptor;
//import com.jfinal.aop.Invocation;
//import com.jfinal.core.ActionException;
//import com.ndasec.base.common.lang.Result;
//import com.ndasec.jfinal.enhance.controller.BaseController;
//import org.apache.log4j.Logger;
//
//public class ExceptionInterceptor implements Interceptor {
//
//    private final Logger logger = Logger.getLogger(ExceptionInterceptor.class);
//
//    @Override
//    public void intercept(Invocation inv) {
//        BaseController controller = (BaseController) inv.getController();
//        try {
//            inv.invoke();
//        } catch (Exception e) {
//            StringBuilder sb = new StringBuilder("\n---Exception Log Begin---\n");
//            sb.append("Controller:").append(inv.getController().getClass().getName()).append("\n");
//            sb.append("Method:").append(inv.getMethodName()).append("\n");
//            sb.append("Exception Type:").append(e.getClass().getName()).append("\n");
//            sb.append("Exception Details:");
//            logger.error(sb.toString(), e);
//            Result object = new Result(false, e.getMessage());
//            if (e instanceof ActionException) {
//                object.set("errorCode", ((ActionException) e).getErrorCode());
//            }
//            controller.renderJson(object);
//            return;
//        }
//    }
//
//
//}
