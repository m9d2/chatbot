package com.yann.chatbot.common;


import com.yann.chatbot.bean.Wechat;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Map;

public class UserContext {
	private static ThreadLocal<HttpSession> localHttpSession = new ThreadLocal<HttpSession>();

	/**
	 * 登陆用户Session key
	 */
	private static final String LOCAL_WECHAT_KEY = "LOCAL_WECHAT_KEY";
	private static final String LOCAL_CONTACT_KEY = "LOCAL_CONTACT_KEY";

	private static HttpSession getHttpSession() {
		return localHttpSession.get();
	}

	public static void setHttpSession(HttpSession httpSession) {
		localHttpSession.set(httpSession);
	}

	/**
	 * 设置用户wetchat对象到Session中
	 * @param wechat
	 */
	public static void setWechat(Wechat wechat){
		HttpSession session = getHttpSession();
		if(session != null){
			session.setAttribute(LOCAL_WECHAT_KEY, wechat);
		}
	}

	public static void setWechat(Map<String, String> map) {
		HttpSession session = getHttpSession();
		Wechat wechat = null;
		if(session != null){
			if(getWechat() != null) {
				wechat = getWechat();
			}else {
				wechat = new Wechat();
			}
			Field[] fields = wechat.getClass().getDeclaredFields();
			for(Field field : fields) {
				field.setAccessible(true);
				String vaule = map.get(field.getName());
				if(vaule != null) {
					try {
						field.set(wechat, vaule);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			session.setAttribute(LOCAL_WECHAT_KEY, wechat);
		}
	}

	public static Wechat getWechat(){
		HttpSession session = getHttpSession();
		if(session != null){
			return (Wechat) session.getAttribute(LOCAL_WECHAT_KEY);
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
