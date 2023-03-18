package com.gy.chatbot.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Message {

    @JsonProperty("ModContactList")
    private List<?> modContactList;
    @JsonProperty("DelContactCount")
    private Integer delContactCount;
    @JsonProperty("AddMsgCount")
    private Integer addMsgCount;
    @JsonProperty("ContinueFlag")
    private Integer continueFlag;
    @JsonProperty("SKey")
    private String sKey;
    @JsonProperty("Profile")
    private Profile profile;
    @JsonProperty("BaseResponse")
    private BaseResponse baseResponse;
    @JsonProperty("DelContactList")
    private List<?> delContactList;
    @JsonProperty("ModChatRoomMemberList")
    private List<?> modChatRoomMemberList;
    @JsonProperty("ModContactCount")
    private Integer modContactCount;
    @JsonProperty("ModChatRoomMemberCount")
    private Integer modChatRoomMemberCount;
    @JsonProperty("SyncCheckKey")
    private SyncKey syncCheckKey;
    @JsonProperty("AddMsgList")
    private List<AddMsgList> addMsgList;
    @JsonProperty("SyncKey")
    private SyncKey syncKey;

}
