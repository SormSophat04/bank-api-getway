package com.lolc.api.getway.dto;

import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.enums.Status;
import com.lolc.api.getway.enums.TransactionStatus;
import com.lolc.api.getway.enums.TransactionType;
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
