package com.agapehill.agape_hill_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MpesaRegisterUrlResponse {

    @JsonProperty("ConversationID")
    private String conversationId;

    @JsonProperty("OriginatorCoversationID")
    private String originatorConversationId;

    @JsonProperty("ResponseDescription")
    private String responseDescription;
}