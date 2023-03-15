package com.gy.chatbot.bean;

import com.alibaba.fastjson.JSONObject;
import com.gy.chatbot.common.utils.Constant;
import com.gy.chatbot.common.utils.DateUtil;
import lombok.Data;

import java.util.Map;

@Data
public class Wechat {

    private String base_uri;

    private String redirect_uri;

    private String webpush_url = Constant.BASE_URL;

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

    /** 微信初始化参数 **/
    private Map<String, Object> baseRequest;
    private JSONObject syncKey;
    private JSONObject user;

}
