package cc.jweb.boot.interceptor;//package com.ndasec.jfinal.interceptor;
//
//import com.jfinal.aop.Interceptor;
//import com.jfinal.aop.Invocation;
//
//public class CrossDomainInterceptor implements Interceptor {
//
//	@Override
//	public void intercept(Invocation inv) {
//		inv.getController().getResponse().addHeader("Access-Control-Allow-Origin", "*");
//		inv.getController().getResponse().addHeader("Access-Control-Allow-Methods", "Get,Post,Put,OPTIONS");
//		inv.getController().getResponse().addHeader("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,Accept");
//		inv.invoke();
//	}
//
//}
