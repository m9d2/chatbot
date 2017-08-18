package com.yann.chatbot.utils;

import java.util.Arrays;
import java.util.List;

public class Constant {

    public static final String CONTENT_ENCODING = "UTF-8";
    public static final String APPID = "wx782c26e4c19acffb";
    public static final String JS_LOGIN_URL = "https://login.weixin.qq.com/jslogin";
    public static final String QRCODE_URL = "https://login.weixin.qq.com/qrcode/";
    public static final String BASE_URL = "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin";
    public static final String PUSH_URL = "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin";
    public static final String CONTENT_TYPE = "application/json;charset=utf-8";
    public static final String TULING_API = "http://www.tuling123.com/openapi/api";
    public static final String[] HOST = {
            "webpush.wx.qq.com",
            "webpush.weixin.qq.com",
            "webpush2.weixin.qq.com",
            "webpush.wechat.com",
            "webpush1.wechat.com",
            "webpush2.wechat.com",
            "webpush1.wechatapp.com"
    };
    public static final List<String> FILTER_USERS = Arrays.asList("newsapp", "fmessage", "filehelper", "weibo", "qqmail",
            "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend",
            "readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin",
            "weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts",
            "notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm",
            "notification_messages");
    public static final String[] TULING_KEY = {
    		"3d7a7a130f6641b4a7f0a8ec711fecea",
    		"d4ef281560c14fbab2c08e9fbe1e6b88",
    		"d78ca86bbb7f4af7b7eabe8aacd2f523",
    		"b00bdd3e45a441b1a563b1ea9017c52a",
    		"5b5411435d6e4f9986c34756da2130b6",
    };
}
