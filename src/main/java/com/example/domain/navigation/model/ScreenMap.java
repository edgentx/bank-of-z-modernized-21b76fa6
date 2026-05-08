package com.example.domain.navigation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * ScreenMap aggregate.
 * Responsible for handling screen rendering logic and legacy BMS constraints.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenMapId;
    private boolean active;

    // Legacy BMS Constraints: Max field length for map name definition
    private static final int MAX_BMS_FIELD_LENGTH = 8;

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
        // Invariant: ScreenMap must be active
        if (!active) {
            throw new IllegalStateException("ScreenMap is not active: " + screenMapId);
        }

        // Validation: Mandatory fields
        if (cmd.screenId() == null || cmd.screenId().isBlank()) {
            throw new IllegalArgumentException("screenId is required");
        }
        if (cmd.deviceType() == null) {
            throw new IllegalArgumentException("deviceType is required");
        }

        // Validation: Legacy BMS field length constraints
        if (cmd.screenId().length() > MAX_BMS_FIELD_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Field lengths must strictly adhere to legacy BMS constraints. ScreenId '%s' exceeds max length of %d",
                    cmd.screenId(), MAX_BMS_FIELD_LENGTH)
            );
        }

        var event = new ScreenRenderedEvent(screenMapId, cmd.screenId(), Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public boolean isActive() {
        return active;
    }
}
