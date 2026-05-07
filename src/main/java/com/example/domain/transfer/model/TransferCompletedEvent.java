package com.example.domain.transfer.model;
import com.example.domain.shared.DomainEvent;
import java.time.Instant;
public record TransferCompletedEvent(String transferId, Instant occurredAt) implements DomainEvent {
  @Override public String type() { return "transfer.completed"; }
  @Override public String aggregateId() { return transferId; }
}
