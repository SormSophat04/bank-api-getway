package com.lolc.api.getway.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "loans")
@EqualsAndHashCode(callSuper = true)
public class Loan extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long loanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId")
    private Customer customer;

    @Column(name = "loan_amount")
    private BigDecimal loanAmount;

    @Column(name = "interest_rate")
    private Double interestRate;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;

    @Column(name = "loan_status")
    private String loanStatus;
}
