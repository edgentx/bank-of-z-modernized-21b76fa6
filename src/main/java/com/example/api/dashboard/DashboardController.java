package com.example.api.dashboard;

import com.example.api.dashboard.dto.DashboardSummary;
import com.example.infrastructure.mongo.account.AccountMongoDataRepository;
import com.example.infrastructure.mongo.customer.CustomerMongoDataRepository;
import com.example.infrastructure.mongo.transaction.TransactionDocument;
import com.example.infrastructure.mongo.transaction.TransactionMongoDataRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Dashboard", description = "Operator dashboard read model")
public class DashboardController {
  private static final String DEFAULT_TELLER = "TELLER-001";
  private static final String DEFAULT_BRANCH = "NYC-1";
  private static final String DEFAULT_CURRENCY = "GBP";

  private final AccountMongoDataRepository accountRepository;
  private final CustomerMongoDataRepository customerRepository;
  private final TransactionMongoDataRepository transactionRepository;

  public DashboardController(
      AccountMongoDataRepository accountRepository,
      CustomerMongoDataRepository customerRepository,
      TransactionMongoDataRepository transactionRepository) {
    this.accountRepository = accountRepository;
    this.customerRepository = customerRepository;
    this.transactionRepository = transactionRepository;
  }

  @GetMapping("/summary")
  @Operation(summary = "Return the teller dashboard summary")
  public DashboardSummary summary(
      @RequestHeader(name = "X-User-Id", defaultValue = DEFAULT_TELLER) String tellerId,
      @RequestHeader(name = "X-Branch-Id", defaultValue = DEFAULT_BRANCH) String branch) {
    List<TransactionDocument> deposits = transactionRepository.findByKindAndPostedTrueAndReversedFalse("deposit");
    List<TransactionDocument> withdrawals = transactionRepository.findByKindAndPostedTrueAndReversedFalse("withdrawal");

    return new DashboardSummary(
        Instant.now(),
        defaultIfBlank(tellerId, DEFAULT_TELLER),
        defaultIfBlank(branch, DEFAULT_BRANCH),
        accountRepository.countByStatus("ACTIVE"),
        accountRepository.countByStatus("CLOSED"),
        customerRepository.countByEnrolledTrue(),
        transactionRepository.countByPostedFalseAndReversedFalse(),
        transactionRepository.countByPostedTrueAndReversedFalse(),
        sumMinorUnits(deposits),
        sumMinorUnits(withdrawals),
        firstCurrency(deposits, withdrawals));
  }

  private static long sumMinorUnits(List<TransactionDocument> transactions) {
    return transactions.stream()
        .map(TransactionDocument::getAmount)
        .mapToLong(DashboardController::toMinorUnits)
        .sum();
  }

  private static long toMinorUnits(BigDecimal amount) {
    if (amount == null) {
      return 0L;
    }
    return amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValue();
  }

  private static String firstCurrency(List<TransactionDocument> deposits, List<TransactionDocument> withdrawals) {
    return Stream.concat(deposits.stream(), withdrawals.stream())
        .map(TransactionDocument::getCurrency)
        .filter(currency -> currency != null && !currency.isBlank())
        .findFirst()
        .map(currency -> currency.toUpperCase(Locale.ROOT))
        .orElse(DEFAULT_CURRENCY);
  }

  private static String defaultIfBlank(String value, String fallback) {
    if (value == null || value.isBlank()) {
      return fallback;
    }
    return value;
  }
}
