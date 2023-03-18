package com.gy.chatbot.service;

import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.kit.http.HttpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gy.chatbot.bean.*;
import com.gy.chatbot.common.ServiceException;
import com.gy.chatbot.common.context.UserContext;
import com.gy.chatbot.common.utils.Constant;
import com.gy.chatbot.common.utils.DateUtil;
import com.gy.chatbot.common.utils.Matchers;
import com.gy.chatbot.common.utils.UrlEncode;
import com.gy.chatbot.task.HandleMsgTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class WechatBotService {

    private final WechatStorage wechat;

    private final RestTemplate restTemplate;

    private final Executor executor;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    public WechatBotService(RestTemplate restTemplate, Executor executor) {
        this.wechat = UserContext.getWechat();
        this.restTemplate = restTemplate;
        this.executor = executor;
    }

    public WechatStorage getWechat() {
        return wechat;
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

    public int login() {
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

    public void start() {
        webWxNewLoginPage();
        wxInit();
        setSyncLine();
        HandleMsgTask handleMsgTask = new HandleMsgTask(this);
        executor.execute(handleMsgTask);
    }

    public int[] syncCheck() {
        return this.syncCheck(wechat, wechat.getWebPushUrl());
    }

    /**
     * 获取消息
     */
    public Message webWxSync() {
        String url = wechat.getBase_uri() + "/webwxsync?" + "&sid=" + wechat.getWxsid() + "skey=" + wechat.getSkey();
        JSONObject params = new JSONObject();
        params.put("BaseRequest", getBaseRequest());
        params.put("SyncKey", objectMapper.convertValue(wechat.getSyncKey(), Map.class));
        params.put("rr", DateUtil.currentTimeSeconds());

        HttpRequest request = HttpRequest.post(url).contentType(Constant.CONTENT_TYPE)
                .header("Cookie", wechat.getCookie()).send(params.toString());
        String res = request.body();
        request.disconnect();

        try {
            Message message = objectMapper.readValue(res, Message.class);
            if (message.getBaseResponse().getRet() == 0) {
                wechat.setSyncKey(message.getSyncKey());
                wechat.setSyncKeyStr(getSyncKeyStr(message.getSyncKey()));
                return message;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean sendText(String toUserName, String content) {
        String url = wechat.getBase_uri() + "/webwxsendmsg?" + "lang=zh_CN" + "&pass_ticket=" + wechat.getPassTicket();
        Map<String, Object> body = new LinkedHashMap<>();
        String clientMsgId = createRandom();
        Map<String, Object> msg = new LinkedHashMap<>();
        msg.put("Type", 1);
        msg.put("Content", content);
        msg.put("FromUserName", wechat.getUser().getUserName());
        msg.put("ToUserName", toUserName);
        msg.put("LocalID", clientMsgId);
        msg.put("ClientMsgId", clientMsgId);

        body.put("BaseRequest", getBaseRequest());
        body.put("Msg", msg);
        restTemplate.postForObject(url, body, ResponseEntity.class);
        return true;
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

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setUin(wechat.getWxuin());
        baseRequest.setSid(wechat.getWxsid());
        baseRequest.setDeviceID(wechat.getDeviceId());
        baseRequest.setSkey(wechat.getSkey());
        wechat.setBaseRequest(baseRequest);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("Uin", wechat.getWxuin());
        params.put("Sid", wechat.getWxsid());
        params.put("Skey", wechat.getSkey());
        params.put("DeviceID", wechat.getDeviceId());
        params.put("BaseRequest", getBaseRequest());

        String resStr = restTemplate.postForObject(url, getRequest(params), String.class);

        try {
            InitResult initResult = objectMapper.readValue(resStr, InitResult.class);
            if (initResult.getBaseResponse().getRet() == 0) {
                wechat.setSyncKey(initResult.getSyncKey());
                wechat.setUser(initResult.getUser());
                wechat.setSyncKeyStr(getSyncKeyStr(initResult.getSyncKey()));
                log.info("登录初始化成功: {}",
                        wechat.getUser().getNickName());
            }
        } catch (JsonProcessingException e) {
            throw new ServiceException("初始化失败，" + e.getMessage());
        }
    }

    /**
     * 选择线路
     */
    private void setSyncLine() {
        int code = 0;
        for (String syncUrl : Constant.HOST) {
            int[] res = this.syncCheck(wechat, syncUrl);
            code = res[0];
            if (code == 0) {
                wechat.setWebPushUrl(syncUrl);
                break;
            }
        }
        if (wechat.getWebPushUrl() == null) {
            throw new ServiceException("Synccheck failed, Code " + code);
        }
    }


    private int[] syncCheck(WechatStorage wechat, String url) {
        url = url + "?r=" + DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5) +
                "&skey=" + wechat.getSkey() +
                "&uin=" + wechat.getWxuin() +
                "&sid=" + wechat.getWxsid() +
                "&deviceid=" + wechat.getDeviceId() +
                "&synckey=" + wechat.getSyncKeyStr() +
                "&_=" + System.currentTimeMillis();
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.COOKIE, wechat.getCookie());
//        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<>(headers);
//        ResponseEntity<String> response = restTemplate.exchange(UrlEncode.encode(url), HttpMethod.GET, formEntity, String.class);
//        String res = response.getBody();

        HttpRequest request = HttpRequest
                .get(UrlEncode.encode(url))
                .header(HttpHeaders.COOKIE, wechat.getCookie());
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

    private HttpEntity<Map<String, Object>> getRequest(Map<String, Object> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, wechat.getCookie());
        headers.set(HttpHeaders.CONTENT_TYPE, Constant.CONTENT_TYPE);
        return new HttpEntity<>(params, headers);
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

    private String createRandom() {
        Double random = Math.random();
        return DateUtil.currentTimeSeconds() + String.valueOf(random).substring(2, 7);
    }

    private String getSyncKeyStr(SyncKey syncKey) {
        List<SyncKey.ListDTO> syncKeys = syncKey.getList();
        StringBuilder sb = new StringBuilder();
        for (SyncKey.ListDTO key : syncKeys) {
            sb.append("|").append(key.getKey()).append("_").append(key.getVal());
        }
        return sb.substring(0);
    }

    private String getBaseRequest() {
        try {
            return objectMapper.writeValueAsString(wechat.getBaseRequest());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
