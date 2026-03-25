package com.lolc.api.getway.repository;

import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByAccount(Account account);
}
