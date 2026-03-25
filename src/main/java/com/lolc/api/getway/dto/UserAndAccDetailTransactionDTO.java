package com.lolc.api.getway.dto;

import com.lolc.api.getway.entity.Transaction;
import lombok.Data;

@Data
public class UserAndAccDetailTransactionDTO {
    private String username;
    private String accountNumber;
    private String accountType;
    private java.math.BigDecimal balance;
    private String currency;
    private String transactionType;
    private java.math.BigDecimal transactionAmount;
    private String description;
    private java.time.LocalDateTime transactionDate;
    private Long referenceNumber;
    private String status;
}
