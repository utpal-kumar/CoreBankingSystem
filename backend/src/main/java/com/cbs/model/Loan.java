package com.cbs.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loanNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private LoanType type;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    private BigDecimal principal;

    private BigDecimal interestRate;

    private Integer termMonths;

    private BigDecimal emiAmount;

    private BigDecimal outstanding;

    private LocalDate startDate;

    private LocalDate nextDueDate;

    @Column(name = "npa_flag")
    private boolean npa;

    @PrePersist
    void onCreate() {
        if (status == null) status = LoanStatus.ACTIVE;
        if (outstanding == null) outstanding = principal;
    }

    public enum LoanType { PERSONAL, HOME, AUTO, BUSINESS }
    public enum LoanStatus { PENDING, ACTIVE, CLOSED, DEFAULTED }
}
