package com.example.domain.reconciliation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Set;

public class ReconciliationStartedEvent implements DomainEvent {
    private final String batchId;
    private final Instant windowStart;
    private final Instant windowEnd;
    private final Set<String> accountIds;
    private final Instant occurredAt;

    public ReconciliationStartedEvent(String batchId, Instant windowStart, Instant windowEnd, Set<String> accountIds, Instant occurredAt) {
        this.batchId = batchId;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.accountIds = accountIds;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "reconciliation.started";
    }

    @Override
    public String aggregateId() {
        return batchId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public Instant windowStart() { return windowStart; }
    public Instant windowEnd() { return windowEnd; }
    public Set<String> accountIds() { return accountIds; }
}