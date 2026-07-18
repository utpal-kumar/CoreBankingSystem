package com.cbs.service;

import com.cbs.model.LedgerEntry;
import com.cbs.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    @Transactional
    public void postDoubleEntry(String glDebit, String glCredit, BigDecimal amount,
                                String currency, String reference, String description) {
        ledgerRepository.save(LedgerEntry.builder()
                .glAccount(glDebit)
                .entryType(LedgerEntry.EntryType.DEBIT)
                .amount(amount)
                .currency(currency)
                .reference(reference)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build());
        ledgerRepository.save(LedgerEntry.builder()
                .glAccount(glCredit)
                .entryType(LedgerEntry.EntryType.CREDIT)
                .amount(amount)
                .currency(currency)
                .reference(reference)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public BigDecimal getBalance(String glAccount) {
        BigDecimal debit = ledgerRepository.findAll().stream()
                .filter(e -> e.getGlAccount().equals(glAccount) && e.getEntryType() == LedgerEntry.EntryType.DEBIT)
                .map(LedgerEntry::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal credit = ledgerRepository.findAll().stream()
                .filter(e -> e.getGlAccount().equals(glAccount) && e.getEntryType() == LedgerEntry.EntryType.CREDIT)
                .map(LedgerEntry::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return credit.subtract(debit);
    }
}
