package com.gy.chatbot.bean;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class WechatContact {

	/** 好友数 **/
	private Integer contactCount;
	/** 群聊数 **/
	private Integer groupCount;
	/** 联系人总数 **/
	private Integer memberCount;
	/** 好友列表 **/
	private JSONArray contactList;
	/** 群列表 **/
	private JSONArray groupList;
	/** 所以联系人 包含好友，群聊， 特殊号， 公众号/服务号 **/
	private JSONArray memberList;

	public void setContactList(JSONArray contactList) {
		JSONArray array = new JSONArray();
		for(Object json : contactList) {
			JSONObject jsonObject = (JSONObject) json;
			jsonObject.put("NickName", jsonObject.getString("NickName").replaceAll("<.*>", ""));
			jsonObject.put("RemarkName", jsonObject.getString("RemarkName").replaceAll("<.*>", ""));
			jsonObject.put("Signature", jsonObject.getString("Signature").replaceAll("<.*>", ""));
			jsonObject.put("Sex", jsonObject.getString("Sex").equals("1") ? "男":"女");
			array.add(jsonObject);
		}
		this.contactList = array;
	}
}
