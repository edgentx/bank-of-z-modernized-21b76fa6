package com.example.domain.shared;
import java.time.Instant;
public interface DomainEvent {
  String type();
  String aggregateId();
  Instant occurredAt();
}
