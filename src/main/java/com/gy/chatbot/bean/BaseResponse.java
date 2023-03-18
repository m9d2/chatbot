package com.gy.chatbot.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BaseResponse {
    @JsonProperty("Ret")
    private Integer ret;
    @JsonProperty("ErrMsg")
    private String errMsg;
}