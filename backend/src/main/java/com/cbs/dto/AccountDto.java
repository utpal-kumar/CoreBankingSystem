package com.cbs.dto;

import com.cbs.model.Account;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountDto {
    private Long id;
    private String accountNumber;
    private String customerName;
    private String customerEmail;
    private String type;
    private String status;
    private BigDecimal balance;
    private String currency;
    private BigDecimal interestRate;
    private String branchCode;

    public static AccountDto from(Account a) {
        return AccountDto.builder()
                .id(a.getId())
                .accountNumber(a.getAccountNumber())
                .customerName(a.getCustomer() != null ? a.getCustomer().getName() : null)
                .customerEmail(a.getCustomer() != null ? a.getCustomer().getEmail() : null)
                .type(a.getType() != null ? a.getType().name() : null)
                .status(a.getStatus() != null ? a.getStatus().name() : null)
                .balance(a.getBalance())
                .currency(a.getCurrency())
                .interestRate(a.getInterestRate())
                .branchCode(a.getBranchCode())
                .build();
    }
}
