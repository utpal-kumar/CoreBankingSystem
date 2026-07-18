package com.cbs.service;

import com.cbs.model.Account;
import com.cbs.model.Customer;
import com.cbs.model.Loan;
import com.cbs.repository.AccountRepository;
import com.cbs.repository.CustomerRepository;
import com.cbs.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Loan originate(Long customerId, Loan.LoanType type, BigDecimal principal,
                          BigDecimal interestRate, Integer termMonths) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        if (customer.getKycStatus() != Customer.KycStatus.VERIFIED) {
            throw new IllegalArgumentException("Customer KYC not verified");
        }
        Account account = accountRepository.findByCustomerId(customerId).stream()
                .filter(a -> a.getType() == Account.AccountType.CURRENT || a.getType() == Account.AccountType.SAVINGS)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Customer has no disbursement account"));

        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal factor = monthlyRate.add(BigDecimal.ONE).pow(termMonths);
        BigDecimal emi = principal.multiply(monthlyRate)
                .multiply(factor)
                .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        Loan loan = Loan.builder()
                .loanNumber("LN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customer(customer)
                .account(account)
                .type(type)
                .status(Loan.LoanStatus.ACTIVE)
                .principal(principal)
                .interestRate(interestRate)
                .termMonths(termMonths)
                .emiAmount(emi)
                .outstanding(principal)
                .startDate(LocalDate.now())
                .nextDueDate(LocalDate.now().plusMonths(1))
                .npa(false)
                .build();
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan payEmi(Long loanId, BigDecimal amount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        if (amount.compareTo(loan.getEmiAmount()) < 0) {
            throw new IllegalArgumentException("Payment below EMI amount");
        }
        BigDecimal newOutstanding = loan.getOutstanding().subtract(amount);
        loan.setOutstanding(newOutstanding.max(BigDecimal.ZERO));
        loan.setNextDueDate(loan.getNextDueDate().plusMonths(1));
        if (loan.getOutstanding().compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(Loan.LoanStatus.CLOSED);
        }
        return loanRepository.save(loan);
    }

    public List<Loan> listByCustomer(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<Loan> findNpa() {
        return loanRepository.findAll().stream().filter(Loan::isNpa).toList();
    }
}
