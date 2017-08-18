package com.yann.chatbot.task;

import com.yann.chatbot.common.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.kit.http.HttpRequest;
import com.yann.chatbot.api.TulingAPI;
import com.yann.chatbot.service.WechatBotService;
import com.yann.chatbot.utils.Constant;
import com.yann.chatbot.bean.Wechat;
import com.yann.chatbot.bean.WechatContact;

public class HandleMsgTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(HandleMsgTask.class);
	private WechatBotService wechatBotService = new WechatBotService();
	private Wechat wechat;
	private WechatContact wContact;
	private static int threadCount = 0;

	public HandleMsgTask() {
		this.wechat = UserContext.getWechat();
		this.wContact = wechatBotService.getContact();
	}

	@Override
	public void run() {
		HandleMsgTask.threadCount = threadCount + 1;
		logger.info("wbot thread count:{}", threadCount);
		logger.info("sync check:[{}][{}]", wechat.getUser().getString("NickName"),
				wechat.getUser().getString("UserName"));
		while (true) {
			/**
			 * 监测心跳 默认为[-1,-1](请求异常) synccheck[0]：0 正常，1100 失败/登出微信
			 * synccheck[1]：0 正常，2 新的消息，7 进入/离开聊天界面
			 */
			int[] synccheck = wechatBotService.synccheck(wechat);

			// 请求异常
			if (synccheck[0] == -1) {
				logger.info("sync check error:[{}][{}]", wechat.getUser().getString("NickName"),
						wechat.getUser().getString("UserName"));
				break;

			}
			// 失败/登出微信
			if (synccheck[0] == 1100 || synccheck[0] == 1101) {
				logger.info("login out:[{}][{}]", wechat.getUser().getString("NickName"),
						wechat.getUser().getString("UserName"));
				break;
			}
			// 正常
			if (synccheck[0] == 0) {
				switch (synccheck[1]) {
				case 0: // 正常
					break;
				case 2: { // 新的消息
					JSONObject object = this.webwxsync(wechat);
					this.handleMsg(wechat, object, wContact);
					break;
				}
				case 3:
					break;
				case 6: {
					JSONObject object = this.webwxsync(wechat);
					this.handleMsg(wechat, object, wContact);
					break;
				}
				case 7: // 进入/离开聊天界面
					break;
				}
			}
			try {
				Thread.sleep(2000); // 等待2s
			} catch (InterruptedException e) {
			}
		}
		threadCount = threadCount - 1;
	}

	/**
	 * 获取消息
	 */
	private JSONObject webwxsync(Wechat wechat) {
		String url = wechat.getBase_uri() + "/webwxsync?" + "skey=" + wechat.getSkey() + "&sid=" + wechat.getWxsid();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("BaseRequest", wechat.getBaseRequest());
		jsonObject.put("SyncKey", wechat.getSyncKey());
		jsonObject.put("rr", DateKit.getCurrentUnixTime());
		HttpRequest request = HttpRequest.post(url).contentType(Constant.CONTENT_TYPE)
				.header("Cookie", wechat.getCookie()).send(jsonObject.toString());
		String res = request.body();
		request.disconnect();

		JSONObject object = JSON.parseObject(res);
		if (null != object) {
			JSONObject BaseResponse = (JSONObject) object.get("BaseResponse");
			if (null != BaseResponse) {
				int ret = BaseResponse.getInteger("Ret");
				if (ret == 0) {
					wechat.setSyncKey(object.getJSONObject("SyncKey"));
					StringBuilder buffer = new StringBuilder();
					JSONArray array = (JSONArray) wechat.getSyncKey().get("List");
					for (Object anArray : array) {
						JSONObject item = (JSONObject) anArray;
						buffer.append("|").append(item.getInteger("Key")).append("_").append(item.getInteger("Val"));
					}
					wechat.setSyncKeyStr(buffer.toString().substring(1));
				}
			}
		}
		return object;
	}

	/**
	 * 消息处理
	 */
	private void handleMsg(Wechat wechat, JSONObject msgJson, WechatContact wechatContact) {
		if (wechat != null && msgJson != null) {
			/** 用户ID **/
			String userName = wechat.getUser().getString("UserName");
			/** 消息列表 **/
			JSONArray addMsgList = msgJson.getJSONArray("AddMsgList");

			// 遍历消息列表
			for (Object anAddMsgList : addMsgList) {
				JSONObject msg = (JSONObject) anAddMsgList;
				/** 消息类型 **/
				int msgType = (int) msg.get("MsgType");
				/** 发送消息用户ID **/
				String fromUserName = msg.getString("FromUserName");
				/** 接收消息用户ID **/
				// String toUserName = msg.getString("ToUserName");
				/** 消息内容 **/
				String content = msg.getString("Content");
				/** 消息时间 **/
				// String createTime = msg.getString("CreateTime");
				// 初始化消息
				if (msgType == 51) {
				}
				// 音乐分享--点赞
				if (msgType == 49) {
					// logger.info("音乐分享...");
				}
				// 图片消息
				else if (msgType == 3) {
					// logger.info("图片消息...");
				}
				// 语音消息
				else if (msgType == 34) {
					// logger.info("语音消息...");
				}
				// 动画表情
				else if (msgType == 42) {
					// logger.info("动画表情...");
				}
				// 系统消息
				else if (msgType == 10000) {
					// logger.info("系统消息...");
				}
				// 文本消息
				else if (msgType == 1) {
					String nickName = this.getNickName(wechat, fromUserName, wechatContact);
					// 自己
					if (fromUserName.equals(userName)) {
					// 特殊号
					} else if (Constant.FILTER_USERS.contains(fromUserName)) {
					// 群聊
					} else if (fromUserName.contains("@@")) {
						String sendUserName = content.split(":")[0];
						String sendUserNickName = this.getNickName(wechat, sendUserName, wechatContact);
						String sendMsg = content.split(":")[1];
						if (sendMsg.contains("@" + wechat.getUser().getString("NickName"))) {
							// 回复
							String reply = TulingAPI.getReply(
									sendMsg.replace("@" + wechat.getUser().getString("NickName"), ""),
									sendUserName.replace("@", "").substring(1, 10), wechat.getCity());
							if (null != reply) {
								this.sendMsg(wechat, reply, fromUserName);
							} else {
								this.sendMsg(wechat, "[流泪]被你玩坏了", fromUserName);
							}
						}
						logger.info("group msg:[{}][{}]:{}", nickName, sendUserNickName, sendMsg.replace("<br/>", ""));
					//好友
					} else {
						logger.info("msg:[{}]:{}", nickName, content);
						// 回复消息
						String reply = TulingAPI.getReply(content, fromUserName.replace("@", "").substring(1, 10),
								wechat.getCity());
						if (null != reply) {
							this.sendMsg(wechat, reply, fromUserName);
						} else {
							this.sendMsg(wechat, "[流泪]被你玩坏了", fromUserName);
						}
					}
				}
			}
		}
	}

	/**
	 * 发送消息
	 */
	private void sendMsg(Wechat wechat, String content, String toUserName) {
		String url = wechat.getBase_uri() + "/webwxsendmsg?" + "lang=zh_CN" + "&pass_ticket=" + wechat.getPassTicket();
		JSONObject body = new JSONObject();
		String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
		JSONObject msg = new JSONObject();
		msg.put("Type", 1);
		msg.put("Content", content);
		msg.put("FromUserName", wechat.getUser().getString("UserName"));
		msg.put("ToUserName", toUserName);
		msg.put("LocalID", clientMsgId);
		msg.put("ClientMsgId", clientMsgId);
		body.put("BaseRequest", wechat.getBaseRequest());
		body.put("Msg", msg);
		HttpRequest request = HttpRequest.post(url).contentType(Constant.CONTENT_TYPE)
				.header("Cookie", wechat.getCookie()).send(body.toString());
		request.body();
		request.disconnect();
	}

	/**
	 * 获取NickName
	 */
	private String getNickName(Wechat wechat, String userName, WechatContact wechatContact) {
		JSONArray arrayContact = wechatContact.getContactList();
		String nickName = null;
		for (Object jsonObject : arrayContact) {
			JSONObject user = (JSONObject) jsonObject;
			nickName = user.getString("UserName");
			if (nickName.equals(userName)) {
				return nickName;
			}
		}
		return nickName;
	}
	
}
