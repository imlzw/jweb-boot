package cc.jweb.boot.interceptor;//package com.ndasec.jfinal.interceptor;
//
//
//import javax.servlet.http.HttpServletRequest;
//
//import com.jfinal.aop.Interceptor;
//import com.jfinal.aop.Invocation;
//
//public class BaseInterceptor implements Interceptor {
//
//
//
//	@Override
//	public void intercept(Invocation inv) {
//		inv.invoke();
//		HttpServletRequest request = inv.getController().getRequest();
//		inv.getController().setAttr("base", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()  + request.getContextPath() + "/");
//	}
//
//}
