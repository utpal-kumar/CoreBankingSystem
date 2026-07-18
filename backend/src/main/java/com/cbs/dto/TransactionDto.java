package com.cbs.dto;

import com.cbs.model.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionDto {
    private Long id;
    private String reference;
    private String fromAccount;
    private String toAccount;
    private String type;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String description;
    private LocalDateTime createdAt;
    private String createdBy;

    public static TransactionDto from(Transaction t) {
        return TransactionDto.builder()
                .id(t.getId())
                .reference(t.getReference())
                .fromAccount(t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null)
                .toAccount(t.getToAccount() != null ? t.getToAccount().getAccountNumber() : null)
                .type(t.getType() != null ? t.getType().name() : null)
                .amount(t.getAmount())
                .currency(t.getCurrency())
                .status(t.getStatus() != null ? t.getStatus().name() : null)
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .createdBy(t.getCreatedBy())
                .build();
    }
}
