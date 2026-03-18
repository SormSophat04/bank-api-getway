package com.lolc.api.getway.repository;

import com.lolc.api.getway.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
