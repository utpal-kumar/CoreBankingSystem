package com.cbs.service;

import com.cbs.model.Account;
import com.cbs.model.Customer;
import com.cbs.repository.AccountRepository;
import com.cbs.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Customer create(Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        return customerRepository.save(customer);
    }

    public Customer get(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    public List<Customer> list() {
        return customerRepository.findAll();
    }

    @Transactional
    public Account openAccount(Long customerId, Account.AccountType type, String currency,
                              BigDecimal initialDeposit, BigDecimal interestRate) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Account account = Account.builder()
                .accountNumber(generateAccountNumber(type))
                .customer(customer)
                .type(type)
                .status(Account.AccountStatus.ACTIVE)
                .balance(initialDeposit)
                .currency(currency)
                .interestRate(interestRate)
                .branchCode("BR001")
                .build();
        return accountRepository.save(account);
    }

    private String generateAccountNumber(Account.AccountType type) {
        String prefix = switch (type) {
            case SAVINGS -> "SAV";
            case CURRENT -> "CUR";
            case FIXED_DEPOSIT -> "FD";
            case RECURRING_DEPOSIT -> "RD";
        };
        return prefix + System.currentTimeMillis() % 1_000_000_000L;
    }
}
