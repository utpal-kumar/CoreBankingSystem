package com.cbs.service;

import com.cbs.model.Account;
import com.cbs.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Account debit(String accountNumber, BigDecimal amount) {
        Account acc = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
        if (acc.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Account is not active: " + accountNumber);
        }
        if (acc.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in " + accountNumber);
        }
        acc.setBalance(acc.getBalance().subtract(amount));
        return accountRepository.save(acc);
    }

    @Transactional
    public Account credit(String accountNumber, BigDecimal amount) {
        Account acc = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
        if (acc.getStatus() == Account.AccountStatus.CLOSED) {
            throw new IllegalArgumentException("Account is closed: " + accountNumber);
        }
        acc.setBalance(acc.getBalance().add(amount));
        return accountRepository.save(acc);
    }
}
