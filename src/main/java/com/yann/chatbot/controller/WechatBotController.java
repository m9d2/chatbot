package com.yann.chatbot.controller;

import com.yann.chatbot.common.GeneralResult;
import com.yann.chatbot.common.ResultCodeEnum;
import com.yann.chatbot.common.UserContext;
import com.yann.chatbot.service.WechatBotService;
import com.yann.chatbot.bean.Wechat;
import com.yann.chatbot.bean.WechatContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/")
public class WechatBotController {

    @Autowired
    private WechatBotService wechatBotService;

    /**
     * 获取微信二维码url
     *
     * @return url
     */
    @ResponseBody
    @RequestMapping("iamge")
    public GeneralResult<String> getIamgeUrl() {
        GeneralResult<String> result = new GeneralResult<>();
        String url = wechatBotService.getQrcodeUrl();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMsg(ResultCodeEnum.SUCCESS.getDesc());
        result.setData(url);
        return result;
    }

    @ResponseBody
    @RequestMapping("login")
    public GeneralResult<Map<String, Object>> getUrl() {
        GeneralResult<Map<String, Object>> result = new GeneralResult<>();
        Map<String, Object> map = wechatBotService.login();
        if(null != map) {
            while (map.get("code").equals("201")) {
                map = wechatBotService.login();
            }
            if (map.get("code").equals("200")) {
                result.setCode(ResultCodeEnum.SUCCESS.getCode());
                result.setMsg(ResultCodeEnum.SUCCESS.getDesc());
                result.setData(map);
                return result;
            }
            if(map.get("code").equals("408")) {
                result.setCode(ResultCodeEnum.OVERTIME.getCode());
                result.setMsg(ResultCodeEnum.OVERTIME.getDesc());
                result.setData(map);
                return result;
            } else {
                result.setCode(ResultCodeEnum.FAILURE.getCode());
                result.setMsg(ResultCodeEnum.FAILURE.getDesc());
                result.setData(map);
                return result;
            }
        }
        result.setCode(ResultCodeEnum.FAILURE.getCode());
        result.setMsg(ResultCodeEnum.FAILURE.getDesc());
        return result;
    }

    @ResponseBody
    @RequestMapping("start")
    public GeneralResult<Wechat> start() {
    	wechatBotService.start();
    	return null;
    }
    
    @ResponseBody
    @RequestMapping("contact")
    public GeneralResult<WechatContact> webwxgetcontact() {
    	GeneralResult<WechatContact> result = new GeneralResult<>();
    	Wechat wechat = UserContext.getWechat();
    	if(wechat == null) {
    		result.setCode(ResultCodeEnum.FAILURE.getCode());
            result.setMsg(ResultCodeEnum.FAILURE.getDesc());
            return result;
    	}
        WechatContact wechatContact = wechatBotService.getContact();
        if (null != wechatContact) {
            result.setCode(ResultCodeEnum.SUCCESS.getCode());
            result.setMsg(ResultCodeEnum.SUCCESS.getDesc());
            result.setData(wechatContact);
        } else {
            result.setCode(ResultCodeEnum.FAILURE.getCode());
            result.setMsg(ResultCodeEnum.FAILURE.getDesc());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping("logout")
    public GeneralResult<String> logout() {
        UserContext.invalidate();
        GeneralResult<String> result = new GeneralResult<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMsg(ResultCodeEnum.SUCCESS.getDesc());
        return result;
    }
}
