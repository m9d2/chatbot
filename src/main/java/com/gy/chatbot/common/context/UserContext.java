package com.gy.chatbot.common.context;


import com.gy.chatbot.bean.WechatStorage;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
public class UserContext {
	private static ThreadLocal<HttpSession> localHttpSession = new ThreadLocal<>();

	/**
	 * 登陆用户Session key
	 */
	private static final String LOCAL_WECHAT_KEY = "LOCAL_WECHAT_KEY";

	private static HttpSession getHttpSession() {
		return localHttpSession.get();
	}

	public static void setHttpSession(HttpSession httpSession) {
		localHttpSession.set(httpSession);
	}

	/**
	 * 设置用户wechat对象到Session中
	 * @param wechat
	 */
	public static void setWechat(WechatStorage wechat){
		HttpSession session = getHttpSession();
		if(session != null){
			session.setAttribute(LOCAL_WECHAT_KEY, wechat);
		}
		log.info("Wechat login - nickname: {}", wechat.getUser());
	}

	public static void setWechat(Map<String, String> map) {
		HttpSession session = getHttpSession();
		WechatStorage wechat = null;
		if(session != null){
			if(getWechat() != null) {
				wechat = getWechat();
			}else {
				wechat = new WechatStorage();
			}
			Field[] fields = wechat.getClass().getDeclaredFields();
			for(Field field : fields) {
				field.setAccessible(true);
				String value = map.get(field.getName());
				if(value != null) {
					try {
						field.set(wechat, value);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			session.setAttribute(LOCAL_WECHAT_KEY, wechat);
		}
	}

	public static WechatStorage getWechat(){
		HttpSession session = getHttpSession();
		if(session != null){
			return (WechatStorage) session.getAttribute(LOCAL_WECHAT_KEY);
		}
		return null;
	}

	private static WechatStorage getWechat(HttpSession httpSession) {
		return (WechatStorage) httpSession.getAttribute(LOCAL_WECHAT_KEY);
	}

	public static String getCookie() {
		WechatStorage wechat = getWechat();
		if (wechat == null) {
			return null;
		}
		return wechat.getCookie();
	}

	/**
	 * 设置用户失效
	 */
	public static void invalidate(){
		HttpSession session = getHttpSession();
		WechatStorage wechat;
		if(session != null){
			wechat = getWechat(session);
			session.invalidate();
			log.info("Wechat logout - nickname: {}", wechat.getUser());
		}
	}
}
