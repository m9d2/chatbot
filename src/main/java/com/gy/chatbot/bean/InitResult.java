package com.gy.chatbot.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class InitResult {

    @JsonProperty("User")
    private User user;
    @JsonProperty("InviteStartCount")
    private Integer inviteStartCount;
    @JsonProperty("MPSubscribeMsgCount")
    private Integer mPSubscribeMsgCount;
    @JsonProperty("SKey")
    private String sKey;
    @JsonProperty("Count")
    private Integer count;
    @JsonProperty("ContactList")
    private List<ContactList> contactList;
    @JsonProperty("ChatSet")
    private String chatSet;
    @JsonProperty("ClientVersion")
    private Integer clientVersion;
    @JsonProperty("GrayScale")
    private Integer grayScale;
    @JsonProperty("BaseResponse")
    private BaseResponse baseResponse;
    @JsonProperty("MPSubscribeMsgList")
    private List<MPSubscribeMsgListDTO> mPSubscribeMsgList;
    @JsonProperty("ClickReportInterval")
    private Integer clickReportInterval;
    @JsonProperty("SyncKey")
    private SyncKey syncKey;
    @JsonProperty("SystemTime")
    private Integer systemTime;

    @NoArgsConstructor
    @Data
    public static class MPSubscribeMsgListDTO {
        @JsonProperty("UserName")
        private String userName;
        @JsonProperty("MPArticleList")
        private List<MPArticleListDTO> mPArticleList;
        @JsonProperty("MPArticleCount")
        private Integer mPArticleCount;
        @JsonProperty("Time")
        private Integer time;
        @JsonProperty("NickName")
        private String nickName;

        @NoArgsConstructor
        @Data
        public static class MPArticleListDTO {
            @JsonProperty("Cover")
            private String cover;
            @JsonProperty("Digest")
            private String digest;
            @JsonProperty("Title")
            private String title;
            @JsonProperty("Url")
            private String url;
        }
    }
}
