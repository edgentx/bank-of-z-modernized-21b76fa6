package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record DefectReportedEvent(String defectId, String title, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId();
    }
}
