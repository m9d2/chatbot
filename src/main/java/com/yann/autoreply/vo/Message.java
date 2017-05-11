package com.yann.autoreply.vo;


public class Message {

    private String MsgType;
    private String Content;
    private String fromUserName;
    private String fromUserNickName;
    private String fromUserRemarkName;

    private String toUserName;
    private String toUserNickName;
    private String toUserRemarkName;

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserNickName() {
        return fromUserNickName;
    }

    public void setFromUserNickName(String fromUserNickName) {
        this.fromUserNickName = fromUserNickName;
    }

    public String getFromUserRemarkName() {
        return fromUserRemarkName;
    }

    public void setFromUserRemarkName(String fromUserRemarkName) {
        this.fromUserRemarkName = fromUserRemarkName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getToUserNickName() {
        return toUserNickName;
    }

    public void setToUserNickName(String toUserNickName) {
        this.toUserNickName = toUserNickName;
    }

    public String getToUserRemarkName() {
        return toUserRemarkName;
    }

    public void setToUserRemarkName(String toUserRemarkName) {
        this.toUserRemarkName = toUserRemarkName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("MsgType='").append(MsgType).append('\'');
        sb.append(", Content='").append(Content).append('\'');
        sb.append(", fromUserName='").append(fromUserName).append('\'');
        sb.append(", fromUserNickName='").append(fromUserNickName).append('\'');
        sb.append(", fromUserRemarkName='").append(fromUserRemarkName).append('\'');
        sb.append(", toUserName='").append(toUserName).append('\'');
        sb.append(", toUserNickName='").append(toUserNickName).append('\'');
        sb.append(", toUserRemarkName='").append(toUserRemarkName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
