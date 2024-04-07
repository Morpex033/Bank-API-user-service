package com.example.demo.service;

import com.example.demo.models.Account;
import com.example.demo.models.User;
import com.example.demo.models.enums.account.Status;
import com.example.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public void createAccount(User user){
        Account account = new Account();
        account.setUser(user);
        account.setCreatedAt(Date.from(Instant.now()));
        account.setStatus(Status.STATUS_ACTIVE);
        account.setBalance(0.00D);
        account.setLastActive(Date.from(Instant.now()));

        this.accountRepository.save(account);
    }

    public Account getAccount(UUID id){
        Account account = this.accountRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Incorrect id"));

        return account;
    }
}
