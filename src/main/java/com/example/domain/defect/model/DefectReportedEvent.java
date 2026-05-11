package com.example.domain.defect;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record DefectReportedEvent(String defectId, String title, String githubUrl, Instant occurredAt) implements DomainEvent {
    @Override public String type() { return "DefectReportedEvent"; }
    @Override public String aggregateId() { return defectId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
