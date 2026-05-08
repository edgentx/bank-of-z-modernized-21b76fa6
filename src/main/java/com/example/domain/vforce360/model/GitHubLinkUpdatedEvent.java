package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record GitHubLinkUpdatedEvent(String defectId, String url, Instant occurredAt) implements DomainEvent {
    @Override public String type() { return "GitHubLinkUpdated"; }
    @Override public String aggregateId() { return defectId; }
}