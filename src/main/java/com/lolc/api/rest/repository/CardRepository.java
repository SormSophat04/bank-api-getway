package com.lolc.api.rest.repository;

import com.lolc.api.rest.entity.Account;
import com.lolc.api.rest.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByAccount(Account account);
}
