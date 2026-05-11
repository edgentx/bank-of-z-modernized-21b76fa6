package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a screen is successfully rendered.
 * Matches the explicit constructor signature required by the error logs:
 * (String, String, String, String, Instant)
 * 
 * Previous attempts failed due to parameter mismatches (6 vs 5 args vs 4 args).
 * We adhere to the signature that appeared in the logs for the 'class' version:
 * required: aggregateId, screenId, deviceType, layoutId, occurredAt
 */
public record ScreenRenderedEvent(
        String aggregateId,
        String screenId,
        String deviceType,
        String layoutId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "screen.rendered";
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
