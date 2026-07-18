package com.cbs.controller;

import com.cbs.common.ApiResponse;
import com.cbs.dto.AccountDto;
import com.cbs.dto.CustomerDto;
import com.cbs.model.Account;
import com.cbs.model.Customer;
import com.cbs.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ApiResponse<CustomerDto> create(@RequestBody @Valid Customer customer) {
        return ApiResponse.ok(CustomerDto.from(customerService.create(customer)));
    }

    @GetMapping
    public ApiResponse<List<CustomerDto>> list() {
        return ApiResponse.ok(customerService.list().stream()
                .map(CustomerDto::from).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerDto> get(@PathVariable Long id) {
        return ApiResponse.ok(CustomerDto.from(customerService.get(id)));
    }

    @PostMapping("/{id}/accounts")
    public ApiResponse<AccountDto> openAccount(@PathVariable Long id,
                                            @RequestBody @Valid OpenAccountRequest req) {
        Account acc = customerService.openAccount(id, req.getType(), req.getCurrency(),
                req.getInitialDeposit(), req.getInterestRate());
        return ApiResponse.ok(AccountDto.from(acc));
    }

    @Data
    public static class OpenAccountRequest {
        @NotNull private Account.AccountType type;
        @NotBlank private String currency;
        @NotNull private BigDecimal initialDeposit;
        private BigDecimal interestRate;
    }
}
