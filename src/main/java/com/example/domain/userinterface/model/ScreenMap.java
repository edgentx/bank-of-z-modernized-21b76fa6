package com.example.domain.userinterface.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * ScreenMap Aggregate.
 * Handles the logic for rendering presentation layouts adapted for user devices.
 * Enforces legacy BMS constraints during the transition period.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenMapId;
    private boolean active;

    public ScreenMap(String screenMapId) {
        this.screenMapId = screenMapId;
        this.active = true;
    }

    @Override
    public String id() {
        return screenMapId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof RenderScreenCmd c) {
            return renderScreen(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> renderScreen(RenderScreenCmd cmd) {
        // Invariant: Mandatory fields validation
        if (cmd.screenId() == null || cmd.screenId().isBlank()) {
            throw new IllegalArgumentException("screenId is mandatory");
        }
        if (cmd.deviceType() == null || cmd.deviceType().isBlank()) {
            throw new IllegalArgumentException("deviceType is mandatory");
        }

        // Invariant: Legacy BMS field length constraints
        // Historically, BMS map names were limited (often 7 chars)
        if (cmd.screenId().length() > 7) {
            throw new IllegalArgumentException("Field length violation: screenId must not exceed 7 characters (Legacy BMS Constraint)");
        }

        var event = new ScreenRenderedEvent(
            this.screenMapId,
            cmd.screenId(),
            cmd.deviceType(),
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public boolean isActive() {
        return active;
    }
}
