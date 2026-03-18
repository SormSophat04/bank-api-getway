package com.lolc.api.getway.entity;

import com.lolc.api.getway.enums.CardType;
import jakarta.persistence.*;
import lombok.Data;

@Data
//@Entity
//@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    Long cardId;

    @Column(name = "card_number")
    Long cardNumber;

    CardType cardType;

    @Column(name = "expiry_date")
    String expiryDate;

    Integer cvv;

    String status;

    @JoinColumn(name = "account_id")
    @ManyToOne(fetch = FetchType.EAGER)
    Account account;
}
