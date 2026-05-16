package com.example.infrastructure.seed;

import com.example.application.AggregateNotFoundException;
import com.example.application.account.AccountAppService;
import com.example.application.customer.CustomerAppService;
import com.example.application.transaction.TransactionAppService;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.customer.model.EnrollCustomerCmd;
import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.PostWithdrawalCmd;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MockDataSeeder implements ApplicationRunner {
  private final CustomerAppService customers;
  private final AccountAppService accounts;
  private final TransactionAppService transactions;
  private final boolean enabled;

  public MockDataSeeder(
      CustomerAppService customers,
      AccountAppService accounts,
      TransactionAppService transactions,
      @Value("${bank.mock-data.enabled:true}") boolean enabled) {
    this.customers = customers;
    this.accounts = accounts;
    this.transactions = transactions;
    this.enabled = enabled;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (!enabled) return;

    for (SeedCustomer customer : seedCustomers()) {
      ensureCustomer(customer);
      for (SeedAccount account : customer.accounts()) {
        ensureAccount(customer.customerId(), account);
        for (SeedTransaction transaction : account.transactions()) {
          ensureTransaction(account.accountId(), transaction);
        }
      }
    }
  }

  private void ensureCustomer(SeedCustomer customer) {
    try {
      customers.findById(customer.customerId());
    } catch (AggregateNotFoundException ex) {
      customers.enroll(new EnrollCustomerCmd(
          customer.customerId(),
          customer.fullName(),
          customer.email(),
          customer.governmentId()));
    }
  }

  private void ensureAccount(String customerId, SeedAccount account) {
    try {
      accounts.findById(account.accountId());
    } catch (AggregateNotFoundException ex) {
      accounts.open(new OpenAccountCmd(
          account.accountId(),
          customerId,
          account.accountType(),
          account.balanceMinor(),
          account.sortCode()));
      accounts.updateStatus(account.accountId(), new UpdateAccountStatusCmd(account.accountId(), "ACTIVE"));
    }
  }

  private void ensureTransaction(String accountId, SeedTransaction transaction) {
    try {
      transactions.findById(transaction.transactionId());
    } catch (AggregateNotFoundException ex) {
      if ("withdrawal".equals(transaction.kind())) {
        transactions.postWithdrawal(new com.example.domain.transaction.model.PostWithdrawalCmd(
            transaction.transactionId(),
            accountId,
            transaction.amount(),
            "GBP"));
      } else {
        transactions.postDeposit(new PostDepositCmd(
            transaction.transactionId(),
            accountId,
            transaction.amount(),
            "GBP"));
      }
    }
  }

  private static List<SeedCustomer> seedCustomers() {
    return List.of(
        new SeedCustomer(
            "CUS-1001",
            "Pat Morgan",
            "pat.morgan@example.com",
            "GOV-PAT-1001",
            List.of(
                new SeedAccount(
                    "20123456",
                    "Current",
                    248_350,
                    "12-34-56",
                    List.of(
                        new SeedTransaction("TX-1001-001", "deposit", new BigDecimal("1250.00")),
                        new SeedTransaction("TX-1001-002", "withdrawal", new BigDecimal("42.25")))))),
        new SeedCustomer(
            "CUS-1002",
            "Jordan Lee",
            "jordan.lee@example.com",
            "GOV-JOR-1002",
            List.of(
                new SeedAccount(
                    "20987654",
                    "Savings",
                    1_052_000,
                    "12-34-56",
                    List.of(new SeedTransaction("TX-1002-001", "deposit", new BigDecimal("500.00")))))),
        new SeedCustomer(
            "CUS-1003",
            "Avery Singh",
            "avery.singh@example.com",
            "GOV-AVE-1003",
            List.of(
                new SeedAccount(
                    "20770011",
                    "Current",
                    72_415,
                    "98-76-54",
                    List.of(new SeedTransaction("TX-1003-001", "withdrawal", new BigDecimal("85.00")))))));
  }

  private record SeedCustomer(
      String customerId,
      String fullName,
      String email,
      String governmentId,
      List<SeedAccount> accounts) {}

  private record SeedAccount(
      String accountId,
      String accountType,
      long balanceMinor,
      String sortCode,
      List<SeedTransaction> transactions) {}

  private record SeedTransaction(
      String transactionId,
      String kind,
      BigDecimal amount) {}
}
