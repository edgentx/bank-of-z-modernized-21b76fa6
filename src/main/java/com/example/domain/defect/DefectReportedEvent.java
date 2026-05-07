package com.example.domain.defect;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

public record DefectReportedEvent(
        String aggregateId,
        String defectId,
        String title,
        String githubUrl,
        Instant occurredAt,
        Map<String, Object> metadata
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
