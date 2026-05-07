package com.example.domain.transfer.model;
import com.example.domain.shared.DomainEvent;
import java.time.Instant;
public record TransferFailedEvent(String transferId, String reason, Instant occurredAt) implements DomainEvent {
  @Override public String type() { return "transfer.failed"; }
  @Override public String aggregateId() { return transferId; }
}
