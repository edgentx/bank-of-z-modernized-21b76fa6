package com.example.domain.userinterface.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * ScreenMap aggregate.
 * Handles the logic for rendering user interface screens.
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
        // Invariant Check: Mandatory fields
        if (cmd.screenId() == null || cmd.screenId().isBlank()) {
            throw new IllegalArgumentException("screenId cannot be null or blank");
        }
        if (cmd.deviceType() == null || cmd.deviceType().isBlank()) {
            throw new IllegalArgumentException("deviceType cannot be null or blank");
        }

        // Invariant Check: Field Lengths (BMS Constraints)
        // Assuming a standard constraint for ID lengths (e.g., max 10 chars for legacy 3270 fields)
        if (cmd.screenId().length() > 10) {
            throw new IllegalArgumentException("screenId length exceeds maximum BMS constraint of 10 characters");
        }

        // If we reached here, invariants are satisfied.
        // Create the event with the correct signature.
        var event = new ScreenRenderedEvent(
            this.screenMapId,
            cmd.screenId(),
            cmd.deviceType(),
            Instant.now()
        );

        this.addEvent(event);
        this.incrementVersion();

        return List.of(event);
    }
}
