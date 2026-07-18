package com.cbs.service;

import com.cbs.model.Account;
import com.cbs.model.Loan;
import com.cbs.model.Transaction;
import com.cbs.repository.AccountRepository;
import com.cbs.repository.LoanRepository;
import com.cbs.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;

    public Map<String, Object> summary() {
        List<Account> accounts = accountRepository.findAll();
        BigDecimal totalDeposits = accounts.stream()
                .filter(a -> a.getType() == Account.AccountType.SAVINGS || a.getType() == Account.AccountType.CURRENT)
                .map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalLoans = loanRepository.findAll().stream()
                .map(Loan::getOutstanding).reduce(BigDecimal.ZERO, BigDecimal::add);
        long customerCount = accounts.stream().map(a -> a.getCustomer().getId()).distinct().count();

        Map<String, Object> result = new HashMap<>();
        result.put("totalAccounts", accounts.size());
        result.put("totalCustomers", customerCount);
        result.put("totalDeposits", totalDeposits);
        result.put("totalLoansOutstanding", totalLoans);
        result.put("totalTransactions", transactionRepository.count());
        return result;
    }
}
