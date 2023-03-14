package com.gy.chatbot.common.context;


import com.gy.chatbot.bean.Wechat;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

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
	public static void setWechat(Wechat wechat){
		HttpSession session = getHttpSession();
		if(session != null){
			session.setAttribute(LOCAL_WECHAT_KEY, wechat);
		}
		log.info("Wechat login - nickname: {}", wechat.getUser());
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

	private static Wechat getWechat(HttpSession httpSession) {
		return (Wechat) httpSession.getAttribute(LOCAL_WECHAT_KEY);
	}

	public static String getCookie() {
		return Objects.requireNonNull(getWechat()).getCookie();
	}

	/**
	 * 设置用户失效
	 */
	public static void invalidate(){
		HttpSession session = getHttpSession();
		Wechat wechat;
		if(session != null){
			wechat = getWechat(session);
			session.invalidate();
			log.info("Wechat logout - nickname: {}", wechat.getUser());
		}
	}
}
