package com.example.domain.userinterface.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Aggregate Root for User Interface / Screen Maps.
 * Handles the rendering logic for specific screens based on device types.
 * ID: S-21
 */
public class ScreenMap extends AggregateRoot {

    private final String screenMapId;

    public ScreenMap(String screenMapId) {
        this.screenMapId = screenMapId;
    }

    @Override
    public String id() {
        return screenMapId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof RenderScreenCmd c) {
            return handleRenderScreen(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleRenderScreen(RenderScreenCmd cmd) {
        // Scenario: All mandatory input fields must be validated before screen submission
        if (cmd.screenId() == null || cmd.screenId().isBlank()) {
            throw new IllegalArgumentException("screenId is mandatory");
        }
        if (cmd.deviceType() == null || cmd.deviceType().isBlank()) {
            throw new IllegalArgumentException("deviceType is mandatory");
        }

        // Scenario: Field lengths must strictly adhere to legacy BMS constraints
        // Assuming legacy BMS screen names cannot exceed 8 characters (3270 standard)
        if (cmd.screenId().length() > 8) {
            throw new IllegalArgumentException("screenId violates legacy BMS length constraints (max 8 chars)");
        }

        // Check input data constraints if provided
        if (cmd.inputData() != null) {
            for (Map.Entry<String, String> entry : cmd.inputData().entrySet()) {
                // Example constraint: field values max 50 chars
                if (entry.getValue() != null && entry.getValue().length() > 50) {
                    throw new IllegalArgumentException("Field value length exceeds legacy BMS constraints");
                }
            }
        }

        var event = new ScreenRenderedEvent(
            "screen.rendered",
            this.screenMapId,
            cmd.screenId(),
            cmd.deviceType(),
            cmd.inputData(),
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}
