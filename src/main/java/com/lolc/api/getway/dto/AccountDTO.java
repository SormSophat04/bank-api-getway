package com.lolc.api.getway.dto;

import lombok.Data;

@Data
public class AccountDTO {
    private Long accountId;
    private String accountNumber;
    private String accountType;
    private String balance;
    private String currency;
    private String status;
    private Long customerId;
}
