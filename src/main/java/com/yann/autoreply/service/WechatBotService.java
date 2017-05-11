package com.yann.autoreply.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.kit.http.HttpRequest;
import com.yann.autoreply.vo.Wechat;
import com.yann.autoreply.task.HandleMsgTask;
import com.yann.autoreply.utils.*;
import com.yann.autoreply.vo.WechatContact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */

@Service
public class WechatBotService {
	
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private static final Logger logger = LoggerFactory.getLogger(WechatBotService.class);

    static {
        System.setProperty("jsse.enableSNIExtension", "false");
    }

    /**
     * 获取二维码
     */
    public Map<String, Object> getQrcodeUrl() {
        Map<String, Object> map = new HashMap<>();
        //获取uuid
        String uuid = this.getUUID();
        if (null != uuid) {
            map.put("uuid", uuid);
            map.put("url", Constant.QRCODE_URL + uuid);
        }
        return map;
    }

    /**
     * 等待扫二维码登录
     */
    public Map<String, Object> login(String uuid) {
        Wechat wechat = new Wechat();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String url = Constant.BASE_URL +
                "/login?" +
                "tip=1" +
                "&uuid=" + uuid;

        Map<String, Object> map = new HashMap<>();
        HttpRequest request = HttpRequest.get(url);
        String res = request.body();
        request.disconnect();
        String code = Matchers.match("window.code=(\\d+);", res);
        if (null != code) {
            if (code.equals("200")) {        //正在登录
                map.put("code", "200");
                Pattern pattern = Pattern.compile("(?<=\").*?(?=\")");
                Matcher m = pattern.matcher(res);
                if (m.find()) {
                    String redirect_uri = m.group(0);
                    wechat.setRedirect_uri(redirect_uri + "&fun=new");
                    String base_uri = redirect_uri.substring(0, redirect_uri.lastIndexOf("/"));
                    wechat.setBase_uri(base_uri);
                    map.put("Wechat", wechat);
                }
                return map;
            } else if (code.equals("201")) {  //成功扫描,未在手机上点击确认以登录
                map.put("code", "201");
                map.put("Wechat", wechat);
                return map;
            } else if (code.equals("408")) {  //登录超时
                map.put("code", "408");
                map.put("Wechat", wechat);
                return map;
            }
        }
        return null;
    }
    
    public void start(Wechat wechat) {
        /**  设置初始化的参数和cookie **/
        this.webwxnewloginpage(wechat);
        /**  初始化 **/
        this.wxInit(wechat);
        /**  设置线路 **/
        this.setSyncLine(wechat);
        /** 获取联系人 **/
        WechatContact wechatContact = this.getContact(wechat);
        /**  开始监听消息 **/
        HandleMsgTask handleMsgTask = new HandleMsgTask(wechat, wechatContact);
        threadPoolTaskExecutor.execute(handleMsgTask);
    }

    /**
     * 检测心跳
     */
    public int[] synccheck(Wechat wechat) {
        return this.synccheck(wechat, null);
    }
    
    /**
     * 设置初始化的参数和cookie
     */
    private void webwxnewloginpage(Wechat wechat) {
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
    private void wxInit(Wechat wechat) {
        String url = wechat.getBase_uri() +
                "/webwxinit?" +
                "pass_ticket=" +
                wechat.getPassTicket() +
                "&skey=" +
                wechat.getSkey() +
                "&r=" +
                System.currentTimeMillis();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Uin", wechat.getWxuin());
        jsonObject.put("Sid", wechat.getWxsid());
        jsonObject.put("Skey", wechat.getSkey());
        jsonObject.put("DeviceID", wechat.getDeviceId());
        wechat.setBaseRequest(jsonObject);
        jsonObject.put("BaseRequest", wechat.getBaseRequest());

        HttpRequest request = HttpRequest.post(url).contentType(Constant.CONTENT_TYPE)
                .header("Cookie", wechat.getCookie()).send(jsonObject.toJSONString());
        String res = request.body();
        request.disconnect();
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
                    for (Object anArray : array) {
                        JSONObject item = (JSONObject) anArray;
                        buffer.append("|").append(item.getInteger("Key")).append("_").append(item.getInteger("Val"));
                    }
                    wechat.setSyncKeyStr(buffer.toString().substring(1));
                }
            }
        }
        if (null != wechat.getUser().getString("UserName")) {
            logger.info("init success:[{}][{}][{}]--{}",
                    wechat.getUser().getString("NickName"),
                    wechat.getProvince().equals(wechat.getCity()) ? wechat.getProvince() : wechat.getProvince()+wechat.getCity(),
                    wechat.getUser().getString("Sex").equals("1") ? "男" : "女",
                    wechat.getUser());
        }
    }

    /**
     * 选择线路
     */
    private void setSyncLine(Wechat wechat) {
        for (String syncUrl : Constant.HOST) {
            int[] res = this.synccheck(wechat, syncUrl);
            if (res[0] == 0) {
                String url = "https://" + syncUrl + "/cgi-bin/mmwebwx-bin";
                wechat.setWebpush_url(url);
                break;
            }
        }
    }

    /**
     * 获取联系人信息
     */
    private WechatContact getContact(Wechat wechat) {
        String url = wechat.getWebpush_url() +
                "/webwxgetcontact?" +
                "pass_ticket=" + wechat.getPassTicket() +
                "&skey=" + wechat.getSkey() +
                "&r=" + System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BaseRequest", wechat.getBaseRequest());
        HttpRequest request = HttpRequest.post(url).contentType(Constant.CONTENT_TYPE)
                .header("Cookie", wechat.getCookie()).send(jsonObject.toJSONString());
        String res = request.body();
        request.disconnect();
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
                            continue;
                        }else if((Integer) contact.get("VerifyFlag") == 24) { // 微信服务号
                        	
                        } else if((Integer) contact.get("VerifyFlag") == 56) { //微信官方账号
                        	
                        }
                        // 群聊
                        else if (contact.getString("UserName").contains("@@")) {
                            groupList.add(contact);
                        }
                        // 自己
                        else if (contact.getString("UserName").equals(wechat.getUser().getString("UserName"))) {
                            continue;
                        }
                        //特殊用户
                        else if (Constant.FILTER_USERS.contains(contact.get("UserName"))) {
                            continue;
                        }
                        contactList.add(contact);
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

    /**
     * 获取uuid
     */
    private String getUUID() {
        String url = Constant.JS_LOGIN_URL +
                "?appid=" + Constant.APPID +        //设置参数:appid
                "&fun=new" +                        //设置参数:fun
                "&lang=zh_CN" +                     //设置参数:lang
                "&_=" + System.currentTimeMillis(); //_的值为时间戳

        HttpRequest request = HttpRequest.get(url);
        request.disconnect();
        String res = request.body();
        if (null != res) {
            String code = Matchers.match("window.QRLogin.code = (\\d+);", res);
            if (null != code) {
                if (code.equals("200")) {
                    return Matchers.match("window.QRLogin.uuid = \"(.*)\";", res);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 检测心跳
     */
    private int[] synccheck(Wechat wechat, String url) {
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

    /**
     * 获取群聊信息
     */
    @SuppressWarnings("unused")
	private WechatContact getGroup(Wechat wechat) {
        String url = Constant.BASE_URL +
                "/webwxbatchgetcontact?" +
                "type=ex" +
                "&pass_ticket=" + wechat.getPassTicket() +
                "&r=" + System.currentTimeMillis();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BaseRequest", wechat.getBaseRequest());
        HttpRequest request = HttpRequest.post(url).contentType(Constant.CONTENT_TYPE)
                .header("Cookie", wechat.getCookie()).send(jsonObject.toJSONString());
        String res = request.body();
        request.disconnect();
        WechatContact wechatContact = new WechatContact();
        JSONObject object = JSONObject.parseObject(res);
        wechatContact.setContactCount((Integer) object.get("Count"));
        wechatContact.setContactList((JSONArray) object.get("List"));
        return wechatContact;
    }
}
