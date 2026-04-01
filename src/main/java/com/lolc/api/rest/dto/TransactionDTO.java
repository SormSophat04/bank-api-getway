package com.lolc.api.rest.dto;

import com.lolc.api.rest.entity.Account;
import com.lolc.api.rest.enums.TransactionStatus;
import com.lolc.api.rest.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDTO {
    private Long transactionId;
    private Account fromAccountId;
    private Account toAccountId;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private TransactionStatus status;
    private Long referenceNumber;
}
