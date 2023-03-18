package com.gy.chatbot.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class SyncKey {
    @JsonProperty("List")
    private List<ListDTO> list;
    @JsonProperty("Count")
    private Integer count;

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        @JsonProperty("Val")
        private Integer val;
        @JsonProperty("Key")
        private Integer key;
    }
}