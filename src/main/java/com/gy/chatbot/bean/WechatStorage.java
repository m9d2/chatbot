package com.gy.chatbot.bean;

import com.gy.chatbot.common.utils.Constant;
import com.gy.chatbot.common.utils.DateUtil;
import lombok.Data;

@Data
public class WechatStorage {

    private String base_uri;

    private String redirect_uri;

    private String webPushUrl;

    private String uuid;

    private String wxsid;

    private String wxuin;

    private String passTicket;

    private String skey;

    private String cookie;

    private String deviceId = "e" + DateUtil.currentTimeSeconds();

    private String syncKeyStr;

    private String province;

    private String city;

    private BaseRequest baseRequest;
    private SyncKey syncKey;
    private User user;

}
