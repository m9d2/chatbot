package com.gy.chatbot.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Profile {
    @JsonProperty("Status")
    private Integer status;
    @JsonProperty("UserName")
    private UserNameDTO userName;
    @JsonProperty("HeadImgUrl")
    private String headImgUrl;
    @JsonProperty("Sex")
    private Integer sex;
    @JsonProperty("PersonalCard")
    private Integer personalCard;
    @JsonProperty("NickName")
    private NickNameDTO nickName;
    @JsonProperty("BindEmail")
    private BindEmailDTO bindEmail;
    @JsonProperty("BitFlag")
    private Integer bitFlag;
    @JsonProperty("Alias")
    private String alias;
    @JsonProperty("Signature")
    private String signature;
    @JsonProperty("BindUin")
    private Integer bindUin;
    @JsonProperty("HeadImgUpdateFlag")
    private Integer headImgUpdateFlag;
    @JsonProperty("BindMobile")
    private BindMobileDTO bindMobile;

    @NoArgsConstructor
    @Data
    public static class UserNameDTO {
        @JsonProperty("Buff")
        private String buff;
    }

    @NoArgsConstructor
    @Data
    public static class NickNameDTO {
        @JsonProperty("Buff")
        private String buff;
    }

    @NoArgsConstructor
    @Data
    public static class BindEmailDTO {
        @JsonProperty("Buff")
        private String buff;
    }

    @NoArgsConstructor
    @Data
    public static class BindMobileDTO {
        @JsonProperty("Buff")
        private String buff;
    }
}