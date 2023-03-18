package com.gy.chatbot.controller;

import com.gy.chatbot.common.context.UserContext;
import com.gy.chatbot.common.utils.Constant;
import com.gy.chatbot.service.WechatBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/")
public class WechatBotController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 获取微信二维码url
     *
     * @return url
     */
    @GetMapping
    public String getImageUrl(Model model) {
        WechatBotService wechatBotService = new WechatBotService(restTemplate, threadPoolTaskExecutor);
        String uuid = wechatBotService.getUUID();
        Map<String, String> map = new HashMap<>();
        map.put("uuid", uuid);
        UserContext.setWechat(map);
        String url = Constant.QRCODE_URL + uuid;
        model.addAttribute("url", url);
        return "index";
    }

    @RequestMapping("login")
    public String getUrl(Model model) {
        WechatBotService wechatBotService = new WechatBotService(restTemplate, threadPoolTaskExecutor);
        int ret = wechatBotService.login();
        if(ret != -1) {
            while (ret == 201) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                wechatBotService.login();
            }
            if (ret == 200) {
                wechatBotService.start();
//                WechatContact wechatContact = wechatBotService.getContact();
//                model.addAttribute(wechatContact);
                return "redirect: /contact";
            }
        }
        return "redirect: /error";
    }

    @RequestMapping("contact")
    public String getContact(Model model) throws IOException {
        return null;
    }

    @GetMapping("send")
    @ResponseBody
    public ResponseEntity<Integer> sendMessage(String msg) {
        return ResponseEntity.ok(200);
    }

    @GetMapping("logout")
    public String logout() {
        UserContext.invalidate();
        return "index";
    }

}
