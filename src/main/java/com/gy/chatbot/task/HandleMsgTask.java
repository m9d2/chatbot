package com.gy.chatbot.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gy.chatbot.bean.Wechat;
import com.gy.chatbot.bean.WechatContact;
import com.gy.chatbot.common.context.UserContext;
import com.gy.chatbot.common.utils.Constant;
import com.gy.chatbot.service.WechatBotService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HandleMsgTask implements Runnable {

    private final WechatBotService wechatBotService;
    private final Wechat wechat;
    private final WechatContact wContact;

    public HandleMsgTask(WechatBotService wechatBotService) {
        this.wechatBotService = wechatBotService;
        this.wechat = wechatBotService.getWechat();
        this.wContact = wechatBotService.getContact();
    }

    @Override
    public void run() {
        while (true) {
            /*
             * 监测心跳 默认为[-1,-1](请求异常)
             * syncCheck[0]：0 正常，1100 失败/登出微信
             * syncCheck[1]：0 正常，2 新的消息，7 进入/离开聊天界面
             */
            int[] syncCheck = wechatBotService.syncCheck();
            log.info("心跳检测...");
            if (syncCheck[0] == -1 || syncCheck[0] == 1100 || syncCheck[0] == 1101) {
                UserContext.invalidate();
                break;
            }
            if (syncCheck[0] == 0) {
                switch (syncCheck[1]) {
                    case 0:
                    case 2: {
                        JSONObject object = wechatBotService.webWxSync();
                        this.handleMsg(object);
                    }
                    case 3:
                    case 6: {
                        JSONObject object = wechatBotService.webWxSync();
                        this.handleMsg(object);
                    }
                    case 7:
                }
            }
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 消息处理
     */
    private void handleMsg(JSONObject msgJson) {
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

                            }
                            log.info("新群聊消息 >> 昵称: {} 来自: {}, 消息内容: {}", nickName, sendUserNickName, sendMsg.replace("<br/>", ""));
                            //好友
                        } else {
                            log.info("新消息 >> 昵称: {}, 消息内容: {}", nickName, content);
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
