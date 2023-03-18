package com.gy.chatbot.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseRequest {

    @JsonProperty("Uin")
    private String uin;

    @JsonProperty("Sid")
    private String sid;

    @JsonProperty("Skey")
    private String skey;

    @JsonProperty("DeviceID")
    private String deviceID;
}
