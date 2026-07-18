package com.cbs.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private BigDecimal balance;

    private String currency;

    private BigDecimal interestRate;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Version
    private Long version;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (balance == null) balance = BigDecimal.ZERO;
        if (currency == null) currency = "USD";
        if (status == null) status = AccountStatus.ACTIVE;
    }

    public enum AccountType { SAVINGS, CURRENT, FIXED_DEPOSIT, RECURRING_DEPOSIT }
    public enum AccountStatus { ACTIVE, DORMANT, CLOSED, FROZEN }
}
