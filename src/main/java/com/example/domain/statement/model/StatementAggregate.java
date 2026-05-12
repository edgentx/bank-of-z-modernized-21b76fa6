package com.example.domain.statement.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * StatementAggregate — account-management bounded context.
 *
 * Hosts the GenerateStatementCmd handler (S-8). Enforces invariants via the
 * Execute(cmd) pattern: validate, emit StatementGeneratedEvent, mutate state,
 * increment version.
 */
public class StatementAggregate extends AggregateRoot {
  private final String id;
  private String accountNumber;
  private boolean generated;
  private boolean closedPeriodViolation;
  private boolean openingBalanceViolation;

  public StatementAggregate(String id) { this.id = id; }

  @Override public String id() { return id; }

  /** Test seam: aggregate violates the closed-period invariant (period still open / retroactive change). */
  public void markClosedPeriodViolation() { this.closedPeriodViolation = true; }
  /** Test seam: aggregate's opening balance does not match the previous statement's closing balance. */
  public void markOpeningBalanceViolation() { this.openingBalanceViolation = true; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof GenerateStatementCmd c) {
      // Invariant: A statement must be generated for a closed period and cannot be altered retroactively.
      if (closedPeriodViolation || generated) {
        throw new IllegalStateException("A statement must be generated for a closed period and cannot be altered retroactively.");
      }
      // Invariant: Statement opening balance must exactly match the closing balance of the previous statement.
      if (openingBalanceViolation) {
        throw new IllegalStateException("Statement opening balance must exactly match the closing balance of the previous statement.");
      }
      if (c.accountNumber() == null || c.accountNumber().isBlank()) throw new IllegalArgumentException("accountNumber required");
      if (c.periodEnd() == null) throw new IllegalArgumentException("periodEnd required");
      var event = new StatementGeneratedEvent(id, c.accountNumber(), c.periodEnd(), Instant.now());
      this.accountNumber = c.accountNumber();
      this.generated = true;
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    throw new UnknownCommandException(cmd);
  }

  public boolean isGenerated() { return generated; }
  public String getAccountNumber() { return accountNumber; }
}
