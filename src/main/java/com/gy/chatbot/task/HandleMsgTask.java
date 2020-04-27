package com.gy.chatbot.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.kit.http.HttpRequest;
import com.gy.chatbot.bean.Wechat;
import com.gy.chatbot.bean.WechatContact;
import com.gy.chatbot.common.api.TuLingAPI;
import com.gy.chatbot.common.context.UserContext;
import com.gy.chatbot.common.utils.Constant;
import com.gy.chatbot.service.WechatBotService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HandleMsgTask implements Runnable {

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
        while (true) {

            /*
             * 监测心跳 默认为[-1,-1](请求异常)
             * syncCheck[0]：0 正常，1100 失败/登出微信
             * syncCheck[1]：0 正常，2 新的消息，7 进入/离开聊天界面
             */
            int[] syncCheck = wechatBotService.syncCheck(wechat);

            // 请求异常
            if (syncCheck[0] == -1) {
                break;

            }
            // 失败/登出微信
            if (syncCheck[0] == 1100 || syncCheck[0] == 1101) {
                log.info("退出登录: 昵称: {}", wechat.getUser().getString("NickName"));
                break;
            }
            // 正常
            if (syncCheck[0] == 0) {
                switch (syncCheck[1]) {
                    case 0: // 正常
                    case 2: { // 新的消息
                        JSONObject object = this.webWxSync(wechat);
                        this.handleMsg(wechat, object, wContact);
                    }
                    case 3:
                    case 6: {
                        JSONObject object = this.webWxSync(wechat);
                        this.handleMsg(wechat, object, wContact);
                    }
                    case 7: // 进入/离开聊天界面
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
    private JSONObject webWxSync(Wechat wechat) {
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
            // 用户ID
            String userName = wechat.getUser().getString("UserName");
            // 消息列表
            JSONArray addMsgList = msgJson.getJSONArray("AddMsgList");

            // 遍历消息列表
            for (Object anAddMsgList : addMsgList) {
                JSONObject msg = (JSONObject) anAddMsgList;
                // 消息类型
                int msgType = (int) msg.get("MsgType");
                // 发送消息用户ID
                String fromUserName = msg.getString("FromUserName");
                // 接收消息用户ID
                 String toUserName = msg.getString("ToUserName");
                //消息内容
                String content = msg.getString("Content");
                // 消息时间
                 String createTime = msg.getString("CreateTime");
                // 初始化消息
                switch (msgType) {
                    //文本消息
                    case 1:
                        String nickName = this.getNickName(fromUserName);
                        // 自己
                        if (fromUserName.equals(userName)) {
                            // 特殊号
                        } else if (Constant.FILTER_USERS.contains(fromUserName)) {
                            // 群聊
                        } else if (fromUserName.contains("@@")) {
                            String sendUserName = content.split(":")[0];
                            String sendUserNickName = this.getNickName(sendUserName);
                            String sendMsg = content.split(":")[1];
                            if (sendMsg.contains("@" + wechat.getUser().getString("NickName"))) {
                                // 回复
                                String reply = TuLingAPI.getReply(
                                        sendMsg.replace("@" + wechat.getUser().getString("NickName"), ""),
                                        sendUserName.replace("@", "").substring(1, 10), wechat.getCity());
                                if (null != reply) {
//								    this.sendMsg(wechat, reply, fromUserName);
                                }
                            }
                            log.info("新群聊消息 >> 昵称: {} 来自: {}, 消息内容: {}", nickName, sendUserNickName, sendMsg.replace("<br/>", ""));
                            //好友
                        } else {
                            log.info("新消息 >> 昵称: {}, 消息内容: {}", nickName, content);
                            // 回复消息
                            String reply = TuLingAPI.getReply(content, fromUserName.replace("@", "").substring(1, 10),
                                    wechat.getCity());
                            if (null != reply) {
//							    this.sendMsg(wechat, reply, fromUserName);
                            }
                        }
                        break;
                    //图片消息
                    case 3:
                        break;
                    //语音消息
                    case 34:
                        break;
                    //动画表情
                    case 42:
                        break;
                    case 49:
                        break;
                    case 51:
                        break;
                    //系统消息
                    case 10000:
                        break;
                    default:
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
    private String getNickName(String userName) {
        final String[] nickName = {null};
        JSONArray members = wContact.getMemberList();
        members.forEach(i -> {
            JSONObject item = (JSONObject) i;
            if (item.getString("UserName").equals(userName)) {
                nickName[0] = item.getString("NickName");
            }
        });
        return nickName[0];
    }

}
