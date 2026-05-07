package com.example.domain.transfer.model;
import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Transfer aggregate (initiate / complete / fail). BANK S-13/S-14/S-15. */
public class TransferAggregate extends AggregateRoot {
  private final String transferId;
  private String fromAccountId;
  private String toAccountId;
  private BigDecimal amount;
  private String currency;
  private Status status = Status.NONE;

  public enum Status { NONE, INITIATED, COMPLETED, FAILED }

  public TransferAggregate(String transferId) { this.transferId = transferId; }
  @Override public String id() { return transferId; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof InitiateTransferCmd c) return initiate(c);
    if (cmd instanceof CompleteTransferCmd c) return complete(c);
    if (cmd instanceof FailTransferCmd c) return fail(c);
    throw new UnknownCommandException(cmd);
  }

  private List<DomainEvent> initiate(InitiateTransferCmd c) {
    if (status != Status.NONE) throw new IllegalStateException("Transfer already exists in state " + status);
    if (c.fromAccountId() == null || c.fromAccountId().isBlank()) throw new IllegalArgumentException("fromAccountId required");
    if (c.toAccountId() == null || c.toAccountId().isBlank()) throw new IllegalArgumentException("toAccountId required");
    if (c.fromAccountId().equals(c.toAccountId())) throw new IllegalArgumentException("from and to accounts must differ");
    if (c.amount() == null || c.amount().signum() <= 0) throw new IllegalArgumentException("amount must be positive");
    if (c.currency() == null || c.currency().length() != 3) throw new IllegalArgumentException("currency must be 3-letter ISO");
    var event = new TransferInitiatedEvent(c.transferId(), c.fromAccountId(), c.toAccountId(), c.amount(), c.currency(), Instant.now());
    this.fromAccountId = c.fromAccountId(); this.toAccountId = c.toAccountId();
    this.amount = c.amount(); this.currency = c.currency(); this.status = Status.INITIATED;
    addEvent(event); incrementVersion();
    return List.of(event);
  }

  private List<DomainEvent> complete(CompleteTransferCmd c) {
    if (status != Status.INITIATED) throw new IllegalStateException("Cannot complete transfer in state " + status);
    var event = new TransferCompletedEvent(c.transferId(), Instant.now());
    status = Status.COMPLETED; addEvent(event); incrementVersion();
    return List.of(event);
  }

  private List<DomainEvent> fail(FailTransferCmd c) {
    if (status != Status.INITIATED) throw new IllegalStateException("Cannot fail transfer in state " + status);
    if (c.reason() == null || c.reason().isBlank()) throw new IllegalArgumentException("reason required");
    var event = new TransferFailedEvent(c.transferId(), c.reason(), Instant.now());
    status = Status.FAILED; addEvent(event); incrementVersion();
    return List.of(event);
  }

  public Status getStatus() { return status; }
  public BigDecimal getAmount() { return amount; }
  public String getFromAccountId() { return fromAccountId; }
  public String getToAccountId() { return toAccountId; }
}
