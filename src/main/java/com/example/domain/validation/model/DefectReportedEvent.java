package com.example.domain.validation.model;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a defect is reported.
 * Contains the formatted body for Slack notification.
 */
public class DefectReportedEvent implements DomainEvent {

    private final String defectId;
    private final String type;
    private final Instant occurredAt;
    private final String body;

    public DefectReportedEvent(String defectId, String body) {
        this.defectId = defectId;
        this.type = "DefectReportedEvent";
        this.occurredAt = Instant.now();
        this.body = body;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String aggregateId() {
        return defectId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    /**
     * The specific payload for Slack integration.
     * Includes the GitHub issue URL as required by VW-454.
     */
    public String body() {
        return body;
    }
}
