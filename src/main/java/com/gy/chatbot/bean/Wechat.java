package com.gy.chatbot.bean;

import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.gy.chatbot.common.utils.Constant;
import lombok.Data;

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

    private String deviceId = "e" + DateKit.getCurrentUnixTime();;

    private String syncKeyStr;

    private String province;

    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    /** 微信初始化参数 **/
    private JSONObject baseRequest;
    private JSONObject syncKey;
    private JSONObject user;

}
