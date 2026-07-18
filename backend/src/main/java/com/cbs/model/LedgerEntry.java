package com.cbs.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gl_account")
    private String glAccount;

    @Column(name = "entry_type")
    @Enumerated(EnumType.STRING)
    private EntryType entryType;

    private BigDecimal amount;

    private String currency;

    private String description;

    private String reference;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public enum EntryType { DEBIT, CREDIT }
}
