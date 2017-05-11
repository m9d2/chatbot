package com.yann.autoreply.controller;

import com.yann.autoreply.common.GeneralResult;
import com.yann.autoreply.common.ResultCodeEnum;
import com.yann.autoreply.vo.Wechat;
import com.yann.autoreply.service.WechatBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class WechatBotController {

    private static Logger logger = LoggerFactory.getLogger(WechatBotController.class);

    @Autowired
    private WechatBotService wechatBotService;

    /**
     * 获取微信二维码url
     *
     * @return url
     */
    @ResponseBody
    @RequestMapping("/iamge")
    public GeneralResult<Map<String, Object>> getIamgeUrl() {
        GeneralResult<Map<String, Object>> result = new GeneralResult<>();
        Map<String, Object> map = wechatBotService.getQrcodeUrl();
        if (null != map) {
            result.setCode(ResultCodeEnum.SUCCESS.getCode());
            result.setMsg(ResultCodeEnum.SUCCESS.getDesc());
            result.setData(map);
        } else {
            result.setCode(ResultCodeEnum.FAILURE.getCode());
            result.setMsg(ResultCodeEnum.FAILURE.getDesc());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public GeneralResult<Map<String, Object>> getUrl(@RequestBody String uuid, HttpServletRequest request) {
        GeneralResult<Map<String, Object>> result = new GeneralResult<>();
        Map<String, Object> map = wechatBotService.login(uuid);
        if(null != map) {
            while (map.get("code").equals("201")) {
                map = wechatBotService.login(uuid);
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
                logger.info("login failed-code[{}]", map.get("code"));
                return result;
            }
        }
        result.setCode(ResultCodeEnum.FAILURE.getCode());
        result.setMsg(ResultCodeEnum.FAILURE.getDesc());
        logger.info("login failed", uuid);
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public void start(@RequestBody Wechat wechat, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("wechat", wechat);
    	wechatBotService.start(wechat);
    }

//    @ResponseBody
//    @RequestMapping(value = "/webwxgetcontact", method = RequestMethod.GET)
//    public GeneralResult<WechatContact> webwxgetcontact(HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        Wechat wechat = (Wechat) session.getAttribute("Wechat");
//        GeneralResult<WechatContact> result = new GeneralResult<>();
//        WechatContact wechatContact = wechatBotService.getContact(wechat);
//        if (null != wechatContact) {
//            result.setCode(ResultCodeEnum.SUCCESS.getCode());
//            result.setMsg(ResultCodeEnum.SUCCESS.getDesc());
//            result.setData(wechatContact);
//        } else {
//            result.setCode(ResultCodeEnum.FAILURE.getCode());
//            result.setMsg("获取联系人失败");
//        }
//        return result;
//    }

}
