package com.gy.chatbot.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AddMsgList {
    @JsonProperty("SubMsgType")
    private Integer subMsgType;
    @JsonProperty("VoiceLength")
    private Integer voiceLength;
    @JsonProperty("FileName")
    private String fileName;
    @JsonProperty("ImgHeight")
    private Integer imgHeight;
    @JsonProperty("ToUserName")
    private String toUserName;
    @JsonProperty("HasProductId")
    private Integer hasProductId;
    @JsonProperty("ImgStatus")
    private Integer imgStatus;
    @JsonProperty("Url")
    private String url;
    @JsonProperty("ImgWidth")
    private Integer imgWidth;
    @JsonProperty("ForwardFlag")
    private Integer forwardFlag;
    @JsonProperty("Status")
    private Integer status;
    @JsonProperty("Ticket")
    private String ticket;
    @JsonProperty("RecommendInfo")
    private RecommendInfoDTO recommendInfo;
    @JsonProperty("CreateTime")
    private Integer createTime;
    @JsonProperty("NewMsgId")
    private Long newMsgId;
    @JsonProperty("MsgType")
    private Integer msgType;
    @JsonProperty("EncryFileName")
    private String encryFileName;
    @JsonProperty("MsgId")
    private String msgId;
    @JsonProperty("StatusNotifyCode")
    private Integer statusNotifyCode;
    @JsonProperty("AppInfo")
    private AppInfoDTO appInfo;
    @JsonProperty("AppMsgType")
    private Integer appMsgType;
    @JsonProperty("PlayLength")
    private Integer playLength;
    @JsonProperty("MediaId")
    private String mediaId;
    @JsonProperty("Content")
    private String content;
    @JsonProperty("StatusNotifyUserName")
    private String statusNotifyUserName;
    @JsonProperty("FromUserName")
    private String fromUserName;
    @JsonProperty("OriContent")
    private String oriContent;
    @JsonProperty("FileSize")
    private String fileSize;

    @NoArgsConstructor
    @Data
    public static class RecommendInfoDTO {
        @JsonProperty("Ticket")
        private String ticket;
        @JsonProperty("UserName")
        private String userName;
        @JsonProperty("Sex")
        private Integer sex;
        @JsonProperty("AttrStatus")
        private Integer attrStatus;
        @JsonProperty("City")
        private String city;
        @JsonProperty("NickName")
        private String nickName;
        @JsonProperty("Scene")
        private Integer scene;
        @JsonProperty("Province")
        private String province;
        @JsonProperty("Content")
        private String content;
        @JsonProperty("Alias")
        private String alias;
        @JsonProperty("Signature")
        private String signature;
        @JsonProperty("OpCode")
        private Integer opCode;
        @JsonProperty("QQNum")
        private Integer qQNum;
        @JsonProperty("VerifyFlag")
        private Integer verifyFlag;
    }

    @NoArgsConstructor
    @Data
    public static class AppInfoDTO {
        @JsonProperty("Type")
        private Integer type;
        @JsonProperty("AppID")
        private String appID;
    }
}