package com.lolc.api.getway.entity;

import com.lolc.api.getway.enums.CardType;
import com.lolc.api.getway.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "cards")
public class Card extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    Long cardId;

    @Column(name = "card_number")
    String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    CardType cardType;

    @Column(name = "expiry_date")
    LocalDate expiryDate;

    @Column(name = "cvv")
    String cvv;

    Status status;

    @JoinColumn(name = "account_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Account account;
}
