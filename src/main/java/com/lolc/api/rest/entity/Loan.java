package com.lolc.api.rest.entity;

import com.lolc.api.rest.enums.Currency;
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

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "interest_rate")
    private Double interestRate;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;

    private BigDecimal principal;

    @Column(name = "total_interest")
    private  BigDecimal totalInterest;

    @Column(name = "total_repayment")
    private BigDecimal totalRepayment;

    @Column(name = "loan_status")
    private String loanStatus;
}
