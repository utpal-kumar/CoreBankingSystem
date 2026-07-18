package com.cbs.dto;

import com.cbs.model.Loan;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LoanDto {
    private Long id;
    private String loanNumber;
    private String customerName;
    private String accountNumber;
    private String type;
    private String status;
    private BigDecimal principal;
    private BigDecimal interestRate;
    private Integer termMonths;
    private BigDecimal emiAmount;
    private BigDecimal outstanding;
    private LocalDate startDate;
    private LocalDate nextDueDate;
    private boolean npa;

    public static LoanDto from(Loan l) {
        return LoanDto.builder()
                .id(l.getId())
                .loanNumber(l.getLoanNumber())
                .customerName(l.getCustomer() != null ? l.getCustomer().getName() : null)
                .accountNumber(l.getAccount() != null ? l.getAccount().getAccountNumber() : null)
                .type(l.getType() != null ? l.getType().name() : null)
                .status(l.getStatus() != null ? l.getStatus().name() : null)
                .principal(l.getPrincipal())
                .interestRate(l.getInterestRate())
                .termMonths(l.getTermMonths())
                .emiAmount(l.getEmiAmount())
                .outstanding(l.getOutstanding())
                .startDate(l.getStartDate())
                .nextDueDate(l.getNextDueDate())
                .npa(l.isNpa())
                .build();
    }
}
