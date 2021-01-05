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
