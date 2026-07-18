package com.cbs.controller;

import com.cbs.common.ApiResponse;
import com.cbs.dto.LoanDto;
import com.cbs.model.Loan;
import com.cbs.service.LoanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ApiResponse<LoanDto> originate(@RequestBody @Valid OriginateRequest req) {
        Loan l = loanService.originate(
                req.getCustomerId(), req.getType(), req.getPrincipal(), req.getInterestRate(), req.getTermMonths());
        return ApiResponse.ok(LoanDto.from(l));
    }

    @PostMapping("/{id}/emi")
    public ApiResponse<LoanDto> payEmi(@PathVariable Long id, @RequestBody @Valid EmiRequest req) {
        return ApiResponse.ok(LoanDto.from(loanService.payEmi(id, req.getAmount())));
    }

    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<LoanDto>> byCustomer(@PathVariable Long customerId) {
        return ApiResponse.ok(loanService.listByCustomer(customerId).stream()
                .map(LoanDto::from).collect(Collectors.toList()));
    }

    @GetMapping("/npa")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<LoanDto>> npa() {
        return ApiResponse.ok(loanService.findNpa().stream()
                .map(LoanDto::from).collect(Collectors.toList()));
    }

    @Data
    public static class OriginateRequest {
        @NotNull private Long customerId;
        @NotNull private Loan.LoanType type;
        @NotNull @Positive private BigDecimal principal;
        @NotNull @Positive private BigDecimal interestRate;
        @NotNull private Integer termMonths;
    }

    @Data
    public static class EmiRequest {
        @NotNull @Positive private BigDecimal amount;
    }
}
