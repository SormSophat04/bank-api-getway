package com.lolc.api.getway.rest;

import com.lolc.api.getway.dto.AccountDTO;
import com.lolc.api.getway.dto.response.AccountResponse;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.mapper.AccountMapper;
import com.lolc.api.getway.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO){
        Account account = accountMapper.toAccount(accountDTO);
        account = accountService.create(account);
        return ResponseEntity.ok(accountMapper.toAccountDTO(account));
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAccounts(){
        List<AccountDTO> accountList = accountService.findAll();
        return ResponseEntity.ok(accountList);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByCustomerId(@PathVariable Long customerId) {
        List<AccountDTO> accounts = accountService.findByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable Long accountId){
        Account account = accountService.findById(accountId);
        return ResponseEntity.ok(account);
    }

    @PutMapping("{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long accountId, @RequestBody AccountDTO accountDTO){
        Account update = accountService.update(accountId, accountDTO);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId){
        accountService.delete(accountId);
        return ResponseEntity.ok("Deleted");
    }
}
