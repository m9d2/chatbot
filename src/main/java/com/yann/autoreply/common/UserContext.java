package com.yann.autoreply.common;


import javax.servlet.http.HttpSession;

import com.yann.autoreply.vo.Wechat;

public class UserContext {
	private static ThreadLocal<HttpSession> localHttpSession = new ThreadLocal<HttpSession>();

	/**
	 * 登陆用户Session key
	 */
	public static final String LOCAL_USER_KEY = "LOCAL_USER_KEY";
	
	public static HttpSession getHttpSession() {
		return localHttpSession.get();
	}

	public static void setHttpSession(HttpSession httpSession) {
		localHttpSession.set(httpSession);
	}

	/**
	 * 设置用户wetchat对象到Session中
	 * @param wechat
	 */
	public static void setSysUser(Wechat wechat){
		HttpSession session = getHttpSession();
		if(session != null){
			session.setAttribute(LOCAL_USER_KEY, wechat);
		}
	}

	public static Wechat getSysUser(){
		HttpSession session = getHttpSession();
		if(session != null){
			return (Wechat) session.getAttribute(LOCAL_USER_KEY);
		}
		return null;
	}

	/**
	 * 设置用户失效
	 */
	public static void invalidate(){
		HttpSession session = getHttpSession();
		if(session != null){
			session.invalidate();
		}
	}
}
