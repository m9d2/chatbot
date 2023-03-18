package com.gy.chatbot.task;

import com.gy.chatbot.bean.AddMsgList;
import com.gy.chatbot.bean.Message;
import com.gy.chatbot.bean.WechatStorage;
import com.gy.chatbot.common.context.UserContext;
import com.gy.chatbot.service.WechatBotService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HandleMsgTask implements Runnable {

    private final WechatBotService wechatBotService;

    private final WechatStorage wechatStorage;

    public HandleMsgTask(WechatBotService wechatBotService) {
        this.wechatBotService = wechatBotService;
        this.wechatStorage = wechatBotService.getWechat();
    }

    @Override
    public void run() {
        while (true) {
            int[] syncCheck = wechatBotService.syncCheck();
            int statusCode = syncCheck[0];
            int msgCode =  syncCheck[1];
            if (statusCode == 1100) {
                log.info("退出登录");
                UserContext.invalidate();
            }
            if (statusCode == -1 || statusCode == 1101) {
                log.error("登录失败");
                UserContext.invalidate();
                break;
            }
            if (statusCode == 0) {
                if (msgCode == 2 || msgCode == 6) {
                    Message message = wechatBotService.webWxSync();
                    this.handleMsg(message);
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
    private void handleMsg(Message message) {
        if (message != null) {
            for (AddMsgList msg : message.getAddMsgList()) {
                if (msg.getMsgType() == 1) {
                    log.info("From: {} >> {}", msg.getFromUserName(), msg.getContent());
                }
            }
        }
    }

}
