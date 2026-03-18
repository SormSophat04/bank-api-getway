package com.lolc.api.getway.entity;

import com.lolc.api.getway.enums.Status;
import com.lolc.api.getway.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
//@Entity
//@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    Long transactionId;

    @Column(name = "from_account_id")
    Long fromAccountId;

    @Column(name = "to_account_id")
    Long toAccountId;

    BigDecimal amount;
    TransactionType type;
    String description;
    Status status;

    @Column(name = "reference_number")
    Long referenceNumber;
}
