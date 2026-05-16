package com.example.api.account.dto;

import com.example.infrastructure.mongo.transaction.TransactionDocument;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Locale;

public record AccountTransactionSummary(
    String transactionId,
    String accountId,
    String postedAt,
    String description,
    String type,
    long amount,
    String currency,
    long runningBalance
) {
  private static final String DEFAULT_CURRENCY = "GBP";

  public static AccountTransactionSummary from(TransactionDocument transaction, long runningBalance) {
    String kind = defaultIfBlank(transaction.getKind(), "transaction");
    return new AccountTransactionSummary(
        transaction.getId(),
        transaction.getAccountId(),
        Instant.now().toString(),
        description(kind),
        type(kind),
        toMinorUnits(transaction.getAmount()),
        defaultIfBlank(transaction.getCurrency(), DEFAULT_CURRENCY).toUpperCase(Locale.ROOT),
        runningBalance);
  }

  public static long toMinorUnits(BigDecimal amount) {
    if (amount == null) {
      return 0L;
    }
    return amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValue();
  }

  private static String type(String kind) {
    return switch (kind.toLowerCase(Locale.ROOT)) {
      case "deposit" -> "DEPOSIT";
      case "withdrawal" -> "WITHDRAWAL";
      default -> kind.toUpperCase(Locale.ROOT).replace('-', '_');
    };
  }

  private static String description(String kind) {
    return switch (kind.toLowerCase(Locale.ROOT)) {
      case "deposit" -> "Deposit";
      case "withdrawal" -> "Withdrawal";
      default -> Character.toUpperCase(kind.charAt(0)) + kind.substring(1);
    };
  }

  private static String defaultIfBlank(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }
}
