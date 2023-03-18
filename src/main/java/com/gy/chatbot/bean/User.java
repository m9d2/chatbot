package com.gy.chatbot.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class User {
    @JsonProperty("UserName")
    private String userName;
    @JsonProperty("StarFriend")
    private Integer starFriend;
    @JsonProperty("HeadImgUrl")
    private String headImgUrl;
    @JsonProperty("Sex")
    private Integer sex;
    @JsonProperty("AppAccountFlag")
    private Integer appAccountFlag;
    @JsonProperty("RemarkPYInitial")
    private String remarkPYInitial;
    @JsonProperty("NickName")
    private String nickName;
    @JsonProperty("HeadImgFlag")
    private Integer headImgFlag;
    @JsonProperty("PYQuanPin")
    private String pYQuanPin;
    @JsonProperty("WebWxPluginSwitch")
    private Integer webWxPluginSwitch;
    @JsonProperty("SnsFlag")
    private Integer snsFlag;
    @JsonProperty("HideInputBarFlag")
    private Integer hideInputBarFlag;
    @JsonProperty("Signature")
    private String signature;
    @JsonProperty("RemarkName")
    private String remarkName;
    @JsonProperty("RemarkPYQuanPin")
    private String remarkPYQuanPin;
    @JsonProperty("Uin")
    private Integer uin;
    @JsonProperty("VerifyFlag")
    private Integer verifyFlag;
    @JsonProperty("PYInitial")
    private String pYInitial;
    @JsonProperty("ContactFlag")
    private Integer contactFlag;
}