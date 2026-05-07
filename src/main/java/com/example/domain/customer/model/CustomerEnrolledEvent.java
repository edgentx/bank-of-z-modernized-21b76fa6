package com.example.domain.customer.model;
import com.example.domain.shared.DomainEvent;
import java.time.Instant;
public record CustomerEnrolledEvent(String customerId, String fullName, String email, String governmentId, Instant occurredAt) implements DomainEvent {
  @Override public String type() { return "customer.enrolled"; }
  @Override public String aggregateId() { return customerId; }
}
