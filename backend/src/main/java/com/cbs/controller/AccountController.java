package com.cbs.controller;

import com.cbs.common.ApiResponse;
import com.cbs.dto.AccountDto;
import com.cbs.model.Account;
import com.cbs.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    @GetMapping
    public ApiResponse<List<AccountDto>> list() {
        return ApiResponse.ok(accountRepository.findAll().stream()
                .map(AccountDto::from).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ApiResponse<AccountDto> get(@PathVariable Long id) {
        Account a = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return ApiResponse.ok(AccountDto.from(a));
    }

    @GetMapping("/number/{accountNumber}")
    public ApiResponse<AccountDto> getByNumber(@PathVariable String accountNumber) {
        Account a = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return ApiResponse.ok(AccountDto.from(a));
    }
}
