package com.example.domain.statement.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class StatementAggregate extends AggregateRoot {
  private final String statementId;
  private String accountId;
  private LocalDate periodStart;
  private LocalDate periodEnd;
  private BigDecimal openingBalance;
  private BigDecimal closingBalance;
  private boolean generated;

  public StatementAggregate(String statementId) {
    this.statementId = statementId;
  }

  @Override public String id() { return statementId; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof GenerateStatementCmd c) {
      if (generated) throw new IllegalStateException("Statement already generated: " + c.statementId());
      if (c.accountId() == null || c.accountId().isBlank()) throw new IllegalArgumentException("accountId required");
      if (c.periodStart() == null || c.periodEnd() == null) throw new IllegalArgumentException("period start/end required");
      if (c.periodEnd().isBefore(c.periodStart())) throw new IllegalArgumentException("periodEnd must be on or after periodStart");
      if (c.openingBalance() == null || c.closingBalance() == null) throw new IllegalArgumentException("opening and closing balances required");
      var event = new StatementGeneratedEvent(c.statementId(), c.accountId(), c.periodStart(), c.periodEnd(),
          c.openingBalance(), c.closingBalance(), Instant.now());
      this.accountId = c.accountId();
      this.periodStart = c.periodStart();
      this.periodEnd = c.periodEnd();
      this.openingBalance = c.openingBalance();
      this.closingBalance = c.closingBalance();
      this.generated = true;
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    throw new UnknownCommandException(cmd);
  }

  public boolean isGenerated() { return generated; }
  public String getAccountId() { return accountId; }
  public LocalDate getPeriodStart() { return periodStart; }
  public LocalDate getPeriodEnd() { return periodEnd; }
  public BigDecimal getOpeningBalance() { return openingBalance; }
  public BigDecimal getClosingBalance() { return closingBalance; }
}
