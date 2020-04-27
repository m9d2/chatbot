package com.gy.chatbot.common.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blade.kit.http.HttpRequest;
import com.gy.chatbot.common.utils.Constant;

public class TuLingAPI {

	private static final String KEY = "key";
	private static final String CONTENT = "info";
	private static final String LOCATION = "loc";
	private static final String USER_ID = "userid";
	private static final String CODE_STR = "code";
	private static final String ERROR_CODE = "40004";
	private static final String TEXT_CODE = "100000";
	private static final String TEXT_STR = "text";
	private static final String URL_CODE = "200000";
	private static final String URL_STR = "url";
	private static final String NEWS_CODE = "302000";
	private static final String MENU_CODE = "308000";


	/**
	 * 获取回复
	 * @param content 发送消息内容
	 * @param userId  用户标识
	 * @param location 位置
	 * @return
	 */
    public static String getReply(String content, String userId, String location) {
    	String reply = null;
    	JSONObject retJson = null;
    	for(String apiKey : Constant.TULING_KEY) {
			JSONObject object = new JSONObject();
	        object.put(KEY,apiKey);
	        object.put(CONTENT, content);
	        object.put(LOCATION, location);
	        object.put(USER_ID, userId);
	        String url = Constant.TULING_API;
	        HttpRequest request = HttpRequest.post(url)
	                .contentType(Constant.CONTENT_TYPE)
	                .send(object.toString());
	        String ret = request.body();
	        request.disconnect();
	        retJson = JSONObject.parseObject(ret);
			if(!retJson.getString(CODE_STR).equals(ERROR_CODE)) {
				reply = handleReply(retJson);
				return reply;
			}
		}
    	return reply;
    }

	//处理json消息
	private static String handleReply(JSONObject applyJson) {
		String code = applyJson.getString(CODE_STR);
		if(code == null) {
			return null;
		}
		//文本类
		if(code.equals(TEXT_CODE)) {
			String text = applyJson.getString(TEXT_STR);
			if(text.contains(";") && text.contains(":")) {
				text = text.replace(":", ":\n");
				text = text.replace(";", ";\n");
				return text;
			}
			return text;
		}
		//链接类
		if(code.equals(URL_CODE)) {
			return applyJson.getString(TEXT_STR) + "\n" + applyJson.getString(URL_STR);
		}
		//新闻类
		if(code.equals(NEWS_CODE)) {
			JSONArray array = applyJson.getJSONArray("list");
			StringBuilder sb = new StringBuilder();
			for(int i=1; i<array.size()+1; i++) {
				JSONObject json = (JSONObject) array.get(i);
				String article = i + ". " + json.getString("article") + "\n" + json.getString("source") +
						"\n" + json.getString("detailurl") + "\n";
				sb.append(article);
				if(i==5) {
					break;
				}
			}
			return sb.toString();
		} 
		//菜谱类
		if(code.equals(MENU_CODE)) {
			JSONArray array = applyJson.getJSONArray("list");
			StringBuffer sb = new StringBuffer();
			for(int i=1; i<array.size()+1; i++) {
				JSONObject json = (JSONObject) array.get(i);
				String article = i + ". " + json.getString("name") + "\n" + json.getString("info") +
						"\n" + json.getString("detailurl") + "\n";
				sb.append(article);
				if(i==5) {
					break;
				}
			}
			return sb.toString();
		}
		return null;
	}

	public static void main(String[] args) {
		String ret = TuLingAPI.getReply("你好", "123",  null);
		System.out.println(ret);
	}
}
