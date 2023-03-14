package com.gy.chatbot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.kit.http.HttpRequest;
import com.gy.chatbot.bean.Wechat;
import com.gy.chatbot.bean.WechatContact;
import com.gy.chatbot.common.context.UserContext;
import com.gy.chatbot.common.utils.Constant;
import com.gy.chatbot.common.utils.CookieUtil;
import com.gy.chatbot.common.utils.Matchers;
import com.gy.chatbot.task.HandleMsgTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class WechatBotService {

    private final Wechat wechat;

    public WechatBotService() {
        this.wechat = UserContext.getWechat();
    }

    public Wechat getWechat() {
        return wechat;
    }

    public int login() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String url = Constant.BASE_URL +
                "/login?" +
                "tip=1" +
                "&uuid=" + wechat.getUuid();

        HttpRequest request = HttpRequest.get(url);
        String res = request.body();
        request.disconnect();
        String code = Matchers.match("window.code=(\\d+);", res);
        if (null != code) {
            switch (code) {
                //登录成功
                case "200":
                    Pattern pattern = Pattern.compile("(?<=\").*?(?=\")");
                    Matcher m = pattern.matcher(res);
                    if (m.find()) {
                        String redirect_uri = m.group(0);
                        wechat.setRedirect_uri(redirect_uri + "&fun=new");
                        String base_uri = redirect_uri.substring(0, redirect_uri.lastIndexOf("/"));
                        wechat.setBase_uri(base_uri);
                    }
                    return 200;
                //成功扫描,未在手机上点击确认以登录
                case "201":
                    return 201;
                //登录超时
                case "408":
                    return 408;
            }
        }
        return 0;
    }
    
    public void start() {
        webWxNewLoginPage();
        wxInit();
        setSyncLine();
        HandleMsgTask handleMsgTask = new HandleMsgTask(this);
        Executors.newFixedThreadPool(1).execute(handleMsgTask);
    }

    public int[] syncCheck() {
        return this.syncCheck(wechat, null);
    }
    
    /**
     * 设置初始化的参数和cookie
     */
    private void webWxNewLoginPage() {
        String url = wechat.getRedirect_uri();
        HttpRequest request = HttpRequest.get(url);
        String res = request.body();
        String cookie = CookieUtil.getCookie(request);
        request.disconnect();
        wechat.setPassTicket(Matchers.match("<pass_ticket>(\\S+)</pass_ticket>", res));
        wechat.setWxsid(Matchers.match("<wxsid>(\\S+)</wxsid>", res));
        wechat.setSkey(Matchers.match("<skey>(\\S+)</skey>", res));
        wechat.setWxuin(Matchers.match("<wxuin>(\\S+)</wxuin>", res));
        wechat.setCookie(cookie);
    }

    /**
     * 微信初始化
     */
    private void wxInit() {
        assert wechat != null;
        String url = wechat.getBase_uri() +
                "/webwxinit?" +
                "pass_ticket=" +
                wechat.getPassTicket() +
                "&skey=" +
                wechat.getSkey() +
                "&r=" +
                System.currentTimeMillis();

        JSONObject params = new JSONObject();
        params.put("Uin", wechat.getWxuin());
        params.put("Sid", wechat.getWxsid());
        params.put("Skey", wechat.getSkey());
        params.put("DeviceID", wechat.getDeviceId());
        wechat.setBaseRequest(params);
        params.put("BaseRequest", wechat.getBaseRequest());

        String res = sendRequest(url, params.toJSONString());
        JSONObject object = JSON.parseObject(res);
        if (null != object) {
            JSONObject BaseResponse = (JSONObject) object.get("BaseResponse");
            if (null != BaseResponse) {
                int ret = BaseResponse.getInteger("Ret");
                if (ret == 0) {
                    wechat.setSyncKey(object.getJSONObject("SyncKey"));
                    wechat.setUser(object.getJSONObject("User"));
                    StringBuilder buffer = new StringBuilder();
                    JSONArray array = (JSONArray) wechat.getSyncKey().get("List");
                    array.forEach(i -> {
                        JSONObject item = (JSONObject) i ;
                        buffer.append("|").append(item.getInteger("Key")).append("_").append(item.getInteger("Val"));
                    });

                    wechat.setSyncKeyStr(buffer.toString().substring(1));
                }
            }
        }
        if (null != wechat.getUser().getString("UserName")) {
            log.info("登录初始化成功: 昵称: {}, 性别: {}",
                    wechat.getUser().getString("NickName"),
                    wechat.getUser().getString("Sex").equals("1") ? "男" : "女");
        }
    }

    /**
     * 选择线路
     */
    private void setSyncLine() {
        for (String syncUrl : Constant.HOST) {
            int[] res = this.syncCheck(wechat, syncUrl);
            if (res[0] == 0) {
                String url = "https://" + syncUrl + "/cgi-bin/mmwebwx-bin";
                assert wechat != null;
                wechat.setWebpush_url(url);
                break;
            }
        }
    }


    public WechatContact getContact() {
        assert wechat != null;
        String url = wechat.getWebpush_url() +
                "/webwxgetcontact?" +
                "pass_ticket=" + wechat.getPassTicket() +
                "&skey=" + wechat.getSkey() +
                "&r=" + System.currentTimeMillis();
        JSONObject params = new JSONObject();
        params.put("BaseRequest", wechat.getBaseRequest());
        String res = sendRequest(url, params.toJSONString());
        WechatContact wechatContact = new WechatContact();

        JSONObject object = JSONObject.parseObject(res);
        JSONObject BaseResponse = object.getJSONObject("BaseResponse");
        if (null != BaseResponse) {
            int ret = BaseResponse.getInteger("Ret");
            if (ret == 0) {
                JSONArray memberList = object.getJSONArray("MemberList");
                JSONArray contactList = new JSONArray();
                JSONArray groupList = new JSONArray();
                if (null != memberList) {
                    for (Object aMemberList : memberList) {
                        JSONObject contact = (JSONObject) aMemberList;
                        // 公众号/服务号
                        if ((Integer) contact.get("VerifyFlag") == 8) {
                        }
                        else if((Integer) contact.get("VerifyFlag") == 24) { // 微信服务号
                        }
                        else if((Integer) contact.get("VerifyFlag") == 56) { //微信官方账号
                        }
                        // 群聊
                        else if (contact.getString("UserName").contains("@@")) {
                            groupList.add(contact);
                        }
                        // 自己
                        else if (contact.getString("UserName").equals(wechat.getUser().getString("UserName"))) {
                        }
                        //特殊用户
                        else if (Constant.FILTER_USERS.contains(contact.get("UserName"))) {
                        }
                        else if((Integer) contact.get("VerifyFlag") == 0){
                        	contactList.add(contact);
                        }
                    }
                    wechatContact.setContactList(contactList);
                    wechatContact.setContactCount(contactList.size());
                    wechatContact.setMemberList(memberList);
                    wechatContact.setMemberCount(memberList.size());
                    wechatContact.setGroupList(groupList);
                    wechatContact.setGroupCount(groupList.size());
                    return wechatContact;
                }
            }
        }
        return null;
    }

    private int[] syncCheck(Wechat wechat, String url) {
        if (null == url) {
            url = wechat.getWebpush_url() + "/synccheck";
        } else {
            url = "https://" + url + "/cgi-bin/mmwebwx-bin/synccheck";
        }
        HttpRequest request = HttpRequest
                .get(url, true, "r", DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5), "skey",
                        wechat.getSkey(), "uin", wechat.getWxuin(), "sid", wechat.getWxsid(), "deviceid",
                        wechat.getDeviceId(), "synckey", wechat.getSyncKeyStr(), "_", System.currentTimeMillis())
                .header("Cookie", wechat.getCookie());
        String res = request.body();
        request.disconnect();
        int[] arr = new int[]{-1, -1};
        if (null == res) {
            return arr;
        }
        String retcode = Matchers.match("retcode:\"(\\d+)\",", res);
        String selector = Matchers.match("selector:\"(\\d+)\"}", res);
        if (null != retcode && null != selector) {
            arr[0] = Integer.parseInt(retcode);
            arr[1] = Integer.parseInt(selector);
            return arr;
        }
        return arr;
    }

	private WechatContact getGroup(Wechat wechat) {
        String url = Constant.BASE_URL +
                "/webwxbatchgetcontact?" +
                "type=ex" +
                "&pass_ticket=" + wechat.getPassTicket() +
                "&r=" + System.currentTimeMillis();

        JSONObject params = new JSONObject();
        String res = sendRequest(url, params.toJSONString());
        WechatContact wechatContact = new WechatContact();
        JSONObject object = JSONObject.parseObject(res);
        wechatContact.setContactCount((Integer) object.get("Count"));
        wechatContact.setContactList((JSONArray) object.get("List"));
        return wechatContact;
    }

    private String sendRequest(String url, String params) {
        HttpRequest request = HttpRequest.post(url).contentType(Constant.CONTENT_TYPE)
                .header("Cookie", UserContext.getCookie()).send(params);
        String ret = request.body();
        request.disconnect();
        return ret;
    }
}
