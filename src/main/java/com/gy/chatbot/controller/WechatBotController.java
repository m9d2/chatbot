package com.gy.chatbot.controller;

import com.gy.chatbot.bean.WechatContact;
import com.gy.chatbot.common.context.UserContext;
import com.gy.chatbot.common.utils.JsonUtils;
import com.gy.chatbot.service.WechatBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@Slf4j
@RequestMapping("/")
public class WechatBotController {

    @Autowired
    private WechatBotService wechatBotService;

    /**
     * 获取微信二维码url
     *
     * @return url
     */
    @GetMapping("index")
    public String getImageUrl(Model model) {
        String url = wechatBotService.getQRCodeUrl();
        model.addAttribute("url", url);
        return "index";
    }

    @RequestMapping("login")
    public String getUrl(Model model) {
        int ret = wechatBotService.login();
        if(ret != 0) {
            while (ret == 201) {
                wechatBotService.login();
            }
            if (ret == 200) {
                wechatBotService.start();
                WechatContact wechatContact = wechatBotService.getContact();
                model.addAttribute(wechatContact);
                return "redirect: /contact";
            }
            if(ret == 408) {
                return "redirect: /error";
            } else {
                return "redirect: /error";
            }
        }
        return "redirect: /error";
    }

    @RequestMapping("contact")
    public String getContact(Model model) throws IOException {
//    	Wechat wechat = UserContext.getWechat();
//    	if(wechat == null) {
//            return "error";
//    	}
//        WechatContact wechatContact = wechatBotService.getContact();
        WechatContact wechatContact = JsonUtils.readJsonFromClassPath("/data.json", WechatContact.class);
        if (null != wechatContact) {
            model.addAttribute("contact", wechatContact);
            return "contact";
        } else {
            return "error";
        }
    }

    @GetMapping("send")
    public Integer sendMessage(String msg) {
        log.info(msg);
        return 200;
    }

    @RequestMapping("logout")
    public String logout() {
        UserContext.invalidate();
        return "index";
    }
}
