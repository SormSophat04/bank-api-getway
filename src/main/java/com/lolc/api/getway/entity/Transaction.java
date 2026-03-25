package com.lolc.api.getway.entity;

import com.lolc.api.getway.enums.TransactionStatus;
import com.lolc.api.getway.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "transactions")
@EqualsAndHashCode(callSuper = true)
public class Transaction extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccountId;

    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type;

    private String description;

    @Column(name = "status")
    private TransactionStatus status;

    @Column(name = "reference_number")
    private Long referenceNumber;
}
