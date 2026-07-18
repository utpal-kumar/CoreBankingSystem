package com.cbs.config;

import com.cbs.model.*;
import com.cbs.repository.*;
import com.cbs.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            authService.createUser("admin", "admin123", Set.of("ADMIN"));
            authService.createUser("teller", "teller123", Set.of("USER"));
        }

        if (customerRepository.count() == 0) {
            Customer c1 = customerRepository.save(Customer.builder()
                    .name("Alice Johnson").email("alice@bank.com").phone("555-1001")
                    .type(Customer.CustomerType.INDIVIDUAL).riskProfile(Customer.RiskProfile.LOW)
                    .address("123 Main St").kycStatus(Customer.KycStatus.VERIFIED).build());

            Customer c2 = customerRepository.save(Customer.builder()
                    .name("Bob Corporation").email("bob@corp.com").phone("555-2002")
                    .type(Customer.CustomerType.CORPORATE).riskProfile(Customer.RiskProfile.MEDIUM)
                    .address("456 Corp Ave").kycStatus(Customer.KycStatus.VERIFIED).build());

            accountRepository.save(Account.builder()
                    .accountNumber("SAV1000001").customer(c1).type(Account.AccountType.SAVINGS)
                    .status(Account.AccountStatus.ACTIVE).balance(new BigDecimal("5000.00"))
                    .currency("USD").interestRate(new BigDecimal("3.5")).branchCode("BR001").build());

            accountRepository.save(Account.builder()
                    .accountNumber("CUR2000001").customer(c2).type(Account.AccountType.CURRENT)
                    .status(Account.AccountStatus.ACTIVE).balance(new BigDecimal("25000.00"))
                    .currency("USD").interestRate(BigDecimal.ZERO).branchCode("BR001").build());

            Loan loan = Loan.builder()
                    .loanNumber("LN00000001").customer(c1)
                    .type(Loan.LoanType.PERSONAL).status(Loan.LoanStatus.ACTIVE)
                    .principal(new BigDecimal("10000.00")).interestRate(new BigDecimal("10.0"))
                    .termMonths(24).emiAmount(new BigDecimal("461.45"))
                    .outstanding(new BigDecimal("10000.00")).npa(false).build();
            loanRepository.save(loan);
        }
    }
}
