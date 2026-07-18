package com.cbs.controller;

import com.cbs.common.ApiResponse;
import com.cbs.dto.TransactionDto;
import com.cbs.model.Transaction;
import com.cbs.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ApiResponse<TransactionDto> transfer(@RequestBody @Valid TransferRequest req) {
        Transaction t = transactionService.transfer(
                req.getFromAccount(), req.getToAccount(), req.getAmount(), req.getDescription());
        return ApiResponse.ok(TransactionDto.from(t));
    }

    @PostMapping("/deposit")
    public ApiResponse<TransactionDto> deposit(@RequestBody @Valid DepositRequest req) {
        Transaction t = transactionService.deposit(
                req.getAccountNumber(), req.getAmount(), req.getDescription());
        return ApiResponse.ok(TransactionDto.from(t));
    }

    @PostMapping("/withdraw")
    public ApiResponse<TransactionDto> withdraw(@RequestBody @Valid WithdrawRequest req) {
        Transaction t = transactionService.withdraw(
                req.getAccountNumber(), req.getAmount(), req.getDescription());
        return ApiResponse.ok(TransactionDto.from(t));
    }

    @PostMapping("/reverse/{reference}")
    public ApiResponse<TransactionDto> reverse(@PathVariable String reference) {
        return ApiResponse.ok(TransactionDto.from(transactionService.reverse(reference)));
    }

    @GetMapping("/account/{accountId}")
    public ApiResponse<List<TransactionDto>> history(@PathVariable Long accountId) {
        return ApiResponse.ok(transactionService.history(accountId).stream()
                .map(TransactionDto::from).collect(Collectors.toList()));
    }

    @Data
    public static class TransferRequest {
        @NotBlank private String fromAccount;
        @NotBlank private String toAccount;
        @NotNull @Positive private BigDecimal amount;
        private String description;
    }

    @Data
    public static class DepositRequest {
        @NotBlank private String accountNumber;
        @NotNull @Positive private BigDecimal amount;
        private String description;
    }

    @Data
    public static class WithdrawRequest {
        @NotBlank private String accountNumber;
        @NotNull @Positive private BigDecimal amount;
        private String description;
    }
}
