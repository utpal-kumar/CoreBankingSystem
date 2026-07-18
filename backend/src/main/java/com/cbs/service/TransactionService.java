package com.cbs.service;

import com.cbs.model.Account;
import com.cbs.model.Transaction;
import com.cbs.repository.AccountRepository;
import com.cbs.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final LedgerService ledgerService;

    private String currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    @Transactional
    public Transaction transfer(String fromAcc, String toAcc, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        String ref = UUID.randomUUID().toString();
        Account from = accountService.debit(fromAcc, amount);
        try {
            Account to = accountService.credit(toAcc, amount);
            Transaction tx = transactionRepository.save(Transaction.builder()
                    .reference(ref)
                    .fromAccount(from)
                    .toAccount(to)
                    .type(Transaction.TransactionType.TRANSFER)
                    .amount(amount)
                    .currency(from.getCurrency())
                    .status(Transaction.TransactionStatus.POSTED)
                    .description(description)
                    .createdAt(LocalDateTime.now())
                    .createdBy(currentUser())
                    .build());
            ledgerService.postDoubleEntry("GL_TRANSFER_OUT", "GL_TRANSFER_IN", amount,
                    from.getCurrency(), ref, "Fund transfer");
            return tx;
        } catch (Exception e) {
            accountService.credit(fromAcc, amount); // rollback debit
            transactionRepository.save(Transaction.builder()
                    .reference(ref)
                    .fromAccount(from)
                    .type(Transaction.TransactionType.TRANSFER)
                    .amount(amount)
                    .currency(from.getCurrency())
                    .status(Transaction.TransactionStatus.FAILED)
                    .description(description)
                    .createdAt(LocalDateTime.now())
                    .createdBy(currentUser())
                    .build());
            throw e;
        }
    }

    @Transactional
    public Transaction deposit(String accountNumber, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        String ref = UUID.randomUUID().toString();
        Account acc = accountService.credit(accountNumber, amount);
        Transaction tx = transactionRepository.save(Transaction.builder()
                .reference(ref)
                .toAccount(acc)
                .type(Transaction.TransactionType.DEPOSIT)
                .amount(amount)
                .currency(acc.getCurrency())
                .status(Transaction.TransactionStatus.POSTED)
                .description(description)
                .createdAt(LocalDateTime.now())
                .createdBy(currentUser())
                .build());
        ledgerService.postDoubleEntry("GL_CASH", "GL_DEPOSIT", amount, acc.getCurrency(), ref, "Deposit");
        return tx;
    }

    @Transactional
    public Transaction withdraw(String accountNumber, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        String ref = UUID.randomUUID().toString();
        Account acc = accountService.debit(accountNumber, amount);
        Transaction tx = transactionRepository.save(Transaction.builder()
                .reference(ref)
                .fromAccount(acc)
                .type(Transaction.TransactionType.WITHDRAWAL)
                .amount(amount)
                .currency(acc.getCurrency())
                .status(Transaction.TransactionStatus.POSTED)
                .description(description)
                .createdAt(LocalDateTime.now())
                .createdBy(currentUser())
                .build());
        ledgerService.postDoubleEntry("GL_WITHDRAWAL", "GL_CASH", amount, acc.getCurrency(), ref, "Withdrawal");
        return tx;
    }

    public List<Transaction> history(Long accountId) {
        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId);
    }

    public List<Transaction> historyByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
        return history(account.getId());
    }

    public Transaction reverse(String reference) {
        List<Transaction> txs = transactionRepository.findByReference(reference);
        if (txs.isEmpty()) throw new IllegalArgumentException("Transaction not found: " + reference);
        Transaction original = txs.get(0);
        if (original.getStatus() == Transaction.TransactionStatus.REVERSED) {
            throw new IllegalArgumentException("Transaction already reversed");
        }
        if (original.getFromAccount() != null && original.getToAccount() != null) {
            accountService.credit(original.getFromAccount().getAccountNumber(), original.getAmount());
            accountService.debit(original.getToAccount().getAccountNumber(), original.getAmount());
        } else if (original.getToAccount() != null) {
            accountService.debit(original.getToAccount().getAccountNumber(), original.getAmount());
        } else if (original.getFromAccount() != null) {
            accountService.credit(original.getFromAccount().getAccountNumber(), original.getAmount());
        }
        original.setStatus(Transaction.TransactionStatus.REVERSED);
        return transactionRepository.save(original);
    }
}
