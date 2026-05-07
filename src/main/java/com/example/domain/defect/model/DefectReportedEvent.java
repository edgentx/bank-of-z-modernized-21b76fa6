package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Event emitted when a defect is reported.
 */
public record DefectReportedEvent(
        String eventId,
        String defectId,
        String title,
        String slackChannelId,
        String messageBody,
        Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent {
        Objects.requireNonNull(eventId, "eventId is required");
        Objects.requireNonNull(defectId, "defectId is required");
    }

    public static DefectReportedEvent create(String defectId, String title, String slackChannelId, String messageBody) {
        return new DefectReportedEvent(
                UUID.randomUUID().toString(),
                defectId,
                title,
                slackChannelId,
                messageBody,
                Instant.now()
        );
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}