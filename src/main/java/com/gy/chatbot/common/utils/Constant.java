package com.gy.chatbot.common.utils;

import java.util.Arrays;
import java.util.List;

public interface Constant {

    String CONTENT_ENCODING = "UTF-8";
    String APPID = "wx782c26e4c19acffb";
    String JS_LOGIN_URL = "https://login.weixin.qq.com/jslogin";
    String QRCODE_URL = "https://login.weixin.qq.com/qrcode/";
    String BASE_URL = "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin";
    String PUSH_URL = "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin";
    String CONTENT_TYPE = "application/json;charset=utf-8";
    String[] HOST = {
            "https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin/synccheck",
            "https://webpush.weixin.qq.com/cgi-bin/mmwebwx-bin/synccheck",
            "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin/synccheck",
            "https://webpush.wechat.com/cgi-bin/mmwebwx-bin/synccheck",
            "https://webpush1.wechat.com/cgi-bin/mmwebwx-bin/synccheck",
            "https://webpush2.wechat.com/cgi-bin/mmwebwx-bin/synccheck"
    };
    List<String> FILTER_USERS = Arrays.asList("newsapp", "fmessage", "filehelper", "weibo", "qqmail",
            "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend",
            "readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin",
            "weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts",
            "notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm",
            "notification_messages");

}
