package com.yann.autoreply.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blade.kit.http.HttpRequest;
import com.yann.autoreply.utils.Constant;

public class TulingAPI {

	//获取json格式数据
    public static JSONObject getReply(String content, String userName, String location) {
    	JSONObject retJson = null;
    	for(String apiKey : Constant.TULING_KEY) {
			JSONObject object = new JSONObject();
	        object.put("key",apiKey);
	        object.put("info", content);
	        object.put("loc", location);
	        object.put("userid", userName);
	        String url = Constant.TULING_API;
	        HttpRequest request = HttpRequest.post(url)
	                .contentType(Constant.CONTENT_TYPE)
	                .send(object.toString());
	        String ret = request.body();
	        request.disconnect();
	        retJson = JSONObject.parseObject(ret);
			if(!retJson.getString("code").equals("40004")) {
				return retJson;
			}
		}
    	return retJson;
    }

	//处理json消息
	public static String handleReply(JSONObject applyJson) {
		String code = applyJson.getString("code");
		if(code == null) {
			return null;
		}
		//文本类
		if(code.equals("100000")) {
			String text = applyJson.getString("text");
			if(text.contains(";") && text.contains(":")) {
				text = text.replace(":", ":\n");
				text = text.replace(";", ";\n");
				return text;
			}
			return text;
		}
		//链接类
		if(code.equals("200000")) {
			String ret = applyJson.getString("text") + "\n" + applyJson.getString("url");
			return ret;
		}
		//新闻类
		if(code.equals("302000")) {
			JSONArray array = applyJson.getJSONArray("list");
			StringBuffer sb = new StringBuffer();
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
		if(code.equals("308000")) {
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
}
