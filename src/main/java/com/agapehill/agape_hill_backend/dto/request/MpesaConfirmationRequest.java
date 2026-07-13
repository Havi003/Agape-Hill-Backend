package com.agapehill.agape_hill_backend.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MpesaConfirmationRequest {

    @JsonProperty("TransactionType")
    private String transactionType;

    @JsonProperty("TransID")
    private String transId;

    @JsonProperty("TransTime")
    private String transTime;

    @JsonProperty("TransAmount")
    private BigDecimal transAmount;

    @JsonProperty("BusinessShortCode")
    private String businessShortCode;

    @JsonProperty("BillRefNumber")
    private String billRefNumber;

    @JsonProperty("InvoiceNumber")
    private String invoiceNumber;

    @JsonProperty("OrgAccountBalance")
    private BigDecimal orgAccountBalance;

    @JsonProperty("ThirdPartyTransID")
    private String thirdPartyTransId;

    @JsonProperty("MSISDN")
    private String msisdn;

    @JsonProperty("FirstName")
    private String firstName;

    @JsonProperty("MiddleName")
    private String middleName;

    @JsonProperty("LastName")
    private String lastName;
}