package com.gy.chatbot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.kit.http.HttpRequest;
import com.blade.kit.http.HttpRequestException;
import com.gy.chatbot.bean.Wechat;
import com.gy.chatbot.bean.WechatContact;
import com.gy.chatbot.common.ServiceException;
import com.gy.chatbot.common.context.UserContext;
import com.gy.chatbot.common.utils.Constant;
import com.gy.chatbot.common.utils.DateUtil;
import com.gy.chatbot.common.utils.Matchers;
import com.gy.chatbot.common.utils.UrlEncode;
import com.gy.chatbot.task.HandleMsgTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class WechatBotService {

    private final Wechat wechat;

    private final RestTemplate restTemplate;

    public WechatBotService(RestTemplate restTemplate) {
        this.wechat = UserContext.getWechat();
        this.restTemplate = restTemplate;
    }

    public Wechat getWechat() {
        return wechat;
    }

    public void start() {
        webWxNewLoginPage();
        wxInit();
        setSyncLine();
        HandleMsgTask handleMsgTask = new HandleMsgTask(this);
        Executors.newFixedThreadPool(1).execute(handleMsgTask);
    }

    public int login() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String url = Constant.BASE_URL + "/login?tip=1&uuid=" + wechat.getUuid();
        String res = restTemplate.getForObject(url, String.class);
        if (res != null) {
            String codeStr= Matchers.match("window.code=(\\d+);", res);
            if (codeStr != null) {
                int code = Integer.parseInt(codeStr);
                switch (code) {
                    //登录成功
                    case 200:
                        Pattern pattern = Pattern.compile("(?<=\").*?(?=\")");
                        Matcher m = pattern.matcher(res);
                        if (m.find()) {
                            String redirect_uri = m.group(0);
                            wechat.setRedirect_uri(redirect_uri + "&fun=new");
                            String base_uri = redirect_uri.substring(0, redirect_uri.lastIndexOf("/"));
                            wechat.setBase_uri(base_uri);
                        }
                        return code;
                    //成功扫描,未在手机上点击确认以登录
                    case 201:
                    //登录超时
                    case 408:
                        return code;
                }
            }
        }
        return -1;
    }

    public int[] syncCheck() {
        return this.syncCheck(wechat, null);
    }
    
    /**
     * 设置初始化的参数和cookie
     */
    private void webWxNewLoginPage() {
        String url = wechat.getRedirect_uri();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String res = response.getBody();
        String cookie = getCookie(response);
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
                DateUtil.currentTimeMillis();

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("Uin", wechat.getWxuin());
        params.put("Sid", wechat.getWxsid());
        params.put("Skey", wechat.getSkey());
        params.put("DeviceID", wechat.getDeviceId());

        Map<String, Object> baseRequest = new LinkedHashMap<>();
        baseRequest.put("Uin", wechat.getWxuin());
        baseRequest.put("Sid", wechat.getWxsid());
        baseRequest.put("Skey", wechat.getSkey());
        baseRequest.put("DeviceID", wechat.getDeviceId());
        wechat.setBaseRequest(baseRequest);
        params.put("BaseRequest", wechat.getBaseRequest());

        String resStr = restTemplate.postForObject(url, params, String.class);
        JSONObject res = JSONObject.parseObject(resStr);
        if (null != res) {
            JSONObject BaseResponse = (JSONObject) res.get("BaseResponse");
            if (null != BaseResponse) {
                int ret = BaseResponse.getInteger("Ret");
                if (ret == 0) {
                    wechat.setSyncKey(res.getJSONObject("SyncKey"));
                    wechat.setUser(res.getJSONObject("User"));
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
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("BaseRequest", wechat.getBaseRequest());
        String res = restTemplate.postForObject(url, params, String.class);
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
        url = url + "?r=" + createRandom() +
                "&skey=" + wechat.getSkey() +
                "&uin=" + wechat.getWxuin() +
                "&sid=" + wechat.getWxsid() +
                "&deviceid=" + wechat.getDeviceId() +
                "&synckey=" + wechat.getSyncKeyStr() +
                "&_=" + DateUtil.currentTimeMillis();
        String res = restTemplate.getForObject(UrlEncode.encode(url), String.class);
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
     * 获取消息
     */
    public JSONObject webWxSync() {
        String url = wechat.getBase_uri() + "/webwxsync?" + "skey=" + wechat.getSkey() + "&sid=" + wechat.getWxsid();
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("BaseRequest", wechat.getBaseRequest());
        params.put("SyncKey", wechat.getSyncKey());
        params.put("rr", DateUtil.currentTimeSeconds());
        String resStr = restTemplate.postForObject(url, params, String.class);
        JSONObject res = JSONObject.parseObject(resStr);
        if (null != res) {
            JSONObject BaseResponse = (JSONObject) res.get("BaseResponse");
            if (null != BaseResponse) {
                int ret = BaseResponse.getInteger("Ret");
                if (ret == 0) {
                    wechat.setSyncKey(res.getJSONObject("SyncKey"));
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
        return res;
    }

	private WechatContact getGroup(Wechat wechat) {
        String url = Constant.BASE_URL +
                "/webwxbatchgetcontact?" +
                "type=ex" +
                "&pass_ticket=" + wechat.getPassTicket() +
                "&r=" + System.currentTimeMillis();
        String res = restTemplate.getForObject(url, String.class);
        WechatContact wechatContact = new WechatContact();
        JSONObject object = JSONObject.parseObject(res);
        wechatContact.setContactCount((Integer) object.get("Count"));
        wechatContact.setContactList((JSONArray) object.get("List"));
        return wechatContact;
    }

    public String getUUID() {
        String url = Constant.JS_LOGIN_URL +
                "?appid=" + Constant.APPID +
                "&fun=new" +
                "&lang=zh_CN" +
                "&_=" + DateUtil.currentTimeMillis();
        String res = restTemplate.getForObject(url, String.class);
        if (null != res) {
            String code = Matchers.match("window.QRLogin.code = (\\d+);", res);
            if (null != code) {
                if (!code.equals("200")) {
                    throw new ServiceException("Js login failed");
                }
            }
        }
        return Matchers.match("window.QRLogin.uuid = \"(.*)\";", res);
    }

    private String getCookie(ResponseEntity<String> response) {
        StringBuilder stringBuffer = new StringBuilder();
        List<String> values = response.getHeaders().get("Set-Cookie");
        if (values != null) {
            for (String value : values) {
                if (value == null) {
                    continue;
                }
                String cookie = value.substring(0, value.indexOf(";") + 1);
                stringBuffer.append(cookie);
            }
        }
        return stringBuffer.toString();
    }

    public String createRandom() {
        Double random = Math.random();
        return DateUtil.currentTimeSeconds() + String.valueOf(random).substring(2, 7);
    }

    private void sendMsg(String content, String toUserName) {
        String url = wechat.getBase_uri() + "/webwxsendmsg?" + "lang=zh_CN" + "&pass_ticket=" + wechat.getPassTicket();
        Map<String, Object> body = new LinkedHashMap<>();
        String clientMsgId = createRandom();
        Map<String, Object> msg = new LinkedHashMap<>();
        msg.put("Type", 1);
        msg.put("Content", content);
        msg.put("FromUserName", wechat.getUser().getString("UserName"));
        msg.put("ToUserName", toUserName);
        msg.put("LocalID", clientMsgId);
        msg.put("ClientMsgId", clientMsgId);
        body.put("BaseRequest", wechat.getBaseRequest());
        body.put("Msg", msg);
        restTemplate.postForObject(url, body, String.class);
    }

    public static void main(String[] args) {
        String u = "r=167889387281114&skey=@crypt_55aba910_e625e94d4bae4bc5214268c22945bc39&uin=247684175&sid=tHSk0Pw/ZkWarSTz&deviceid=e1678893797&synckey=1_827106158|2_827107216|3_827107137|1000_1678888802&_=1678893872682";
        try {
            System.out.println(URLEncoder.encode(u, "GBK"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
