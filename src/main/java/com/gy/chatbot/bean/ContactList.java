package com.gy.chatbot.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ContactList {
    @JsonProperty("ChatRoomId")
    private Integer chatRoomId;
    @JsonProperty("Sex")
    private Integer sex;
    @JsonProperty("AttrStatus")
    private Integer attrStatus;
    @JsonProperty("Statues")
    private Integer statues;
    @JsonProperty("PYQuanPin")
    private String pYQuanPin;
    @JsonProperty("EncryChatRoomId")
    private String encryChatRoomId;
    @JsonProperty("DisplayName")
    private String displayName;
    @JsonProperty("VerifyFlag")
    private Integer verifyFlag;
    @JsonProperty("UniFriend")
    private Integer uniFriend;
    @JsonProperty("ContactFlag")
    private Integer contactFlag;
    @JsonProperty("UserName")
    private String userName;
    @JsonProperty("MemberList")
    private List<?> memberList;
    @JsonProperty("StarFriend")
    private Integer starFriend;
    @JsonProperty("HeadImgUrl")
    private String headImgUrl;
    @JsonProperty("AppAccountFlag")
    private Integer appAccountFlag;
    @JsonProperty("MemberCount")
    private Integer memberCount;
    @JsonProperty("RemarkPYInitial")
    private String remarkPYInitial;
    @JsonProperty("City")
    private String city;
    @JsonProperty("NickName")
    private String nickName;
    @JsonProperty("Province")
    private String province;
    @JsonProperty("SnsFlag")
    private Integer snsFlag;
    @JsonProperty("Alias")
    private String alias;
    @JsonProperty("KeyWord")
    private String keyWord;
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
    @JsonProperty("OwnerUin")
    private Integer ownerUin;
    @JsonProperty("IsOwner")
    private Integer isOwner;
    @JsonProperty("PYInitial")
    private String pYInitial;
}