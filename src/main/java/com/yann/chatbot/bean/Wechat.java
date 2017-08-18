package com.yann.chatbot.bean;

import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.yann.chatbot.utils.Constant;

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

    public String getBase_uri() {
        return base_uri;
    }

    public void setBase_uri(String base_uri) {
        this.base_uri = base_uri;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    public String getWebpush_url() {
        return webpush_url;
    }

    public void setWebpush_url(String webpush_url) {
        this.webpush_url = webpush_url;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getWxsid() {
        return wxsid;
    }

    public void setWxsid(String wxsid) {
        this.wxsid = wxsid;
    }

    public String getWxuin() {
        return wxuin;
    }

    public void setWxuin(String wxuin) {
        this.wxuin = wxuin;
    }

    public String getPassTicket() {
        return passTicket;
    }

    public void setPassTicket(String passTicket) {
        this.passTicket = passTicket;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSyncKeyStr() {
        return syncKeyStr;
    }

    public void setSyncKeyStr(String syncKeyStr) {
        this.syncKeyStr = syncKeyStr;
    }

    public JSONObject getBaseRequest() {
        return baseRequest;
    }

    public void setBaseRequest(JSONObject baseRequest) {
        this.baseRequest = baseRequest;
    }

    public JSONObject getSyncKey() {
        return syncKey;
    }

    public void setSyncKey(JSONObject syncKey) {
        this.syncKey = syncKey;
    }

    public JSONObject getUser() {
        return user;
    }

    public void setUser(JSONObject user) {
        this.user = user;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Wechat{");
        sb.append("base_uri='").append(base_uri).append('\'');
        sb.append(", redirect_uri='").append(redirect_uri).append('\'');
        sb.append(", webpush_url='").append(webpush_url).append('\'');
        sb.append(", uuid='").append(uuid).append('\'');
        sb.append(", wxsid='").append(wxsid).append('\'');
        sb.append(", wxuin='").append(wxuin).append('\'');
        sb.append(", passTicket='").append(passTicket).append('\'');
        sb.append(", skey='").append(skey).append('\'');
        sb.append(", cookie='").append(cookie).append('\'');
        sb.append(", deviceId='").append(deviceId).append('\'');
        sb.append(", syncKeyStr='").append(syncKeyStr).append('\'');
        sb.append(", province='").append(province).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", baseRequest=").append(baseRequest);
        sb.append(", syncKey=").append(syncKey);
        sb.append(", user=").append(user);
        sb.append('}');
        return sb.toString();
    }
}
