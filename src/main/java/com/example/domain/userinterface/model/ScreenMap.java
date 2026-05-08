package com.example.domain.userinterface.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScreenMap Aggregate.
 * Handles the presentation layout logic for the Bank of Z modernization effort.
 */
public class ScreenMap extends AggregateRoot {

    private final String id;
    private String currentScreenId;
    private String currentDeviceType;

    // BMS Constraints (Legacy 3270)
    private static final int MAX_FIELD_LENGTH = 80; // Typical BMS line length constraint for transition period

    public ScreenMap(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof RenderScreenCmd c) {
            return handleRenderScreen(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleRenderScreen(RenderScreenCmd cmd) {
        // 1. Validate mandatory inputs
        if (cmd.screenId() == null || cmd.screenId().isBlank()) {
            throw new IllegalArgumentException("screenId is mandatory");
        }
        if (cmd.deviceType() == null || cmd.deviceType().isBlank()) {
            throw new IllegalArgumentException("deviceType is mandatory");
        }

        // 2. Validate Legacy BMS Constraints (Field Lengths)
        // Assuming the command might contain fields to be rendered or metadata that exceeds limits.
        // For this aggregate, we validate that the identifiers fit within the legacy buffer constraints.
        if (cmd.screenId().length() > MAX_FIELD_LENGTH) {
            throw new IllegalArgumentException("Field length violation: screenId exceeds legacy BMS constraint of " + MAX_FIELD_LENGTH);
        }
        if (cmd.deviceType().length() > MAX_FIELD_LENGTH) {
            throw new IllegalArgumentException("Field length violation: deviceType exceeds legacy BMS constraint of " + MAX_FIELD_LENGTH);
        }

        // 3. Generate Layout
        Map<String, Object> layout = generateLayout(cmd.screenId(), cmd.deviceType());

        // 4. Apply Event
        ScreenRenderedEvent event = new ScreenRenderedEvent(
            java.util.UUID.randomUUID().toString(), // eventId
            "ScreenRenderedEvent",
            this.id,
            Instant.now(),
            cmd.screenId(),
            cmd.deviceType(),
            layout
        );

        this.currentScreenId = cmd.screenId();
        this.currentDeviceType = cmd.deviceType();

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private Map<String, Object> generateLayout(String screenId, String deviceType) {
        Map<String, Object> layout = new HashMap<>();
        layout.put("screenId", screenId);
        layout.put("deviceType", deviceType);
        layout.put("timestamp", Instant.now().toString());
        
        // Stubbed layout logic based on device type
        if ("3270".equalsIgnoreCase(deviceType)) {
            layout.put("format", "text-plain");
            layout.put("width", 80);
            layout.put("depth", 24);
        } else {
            layout.put("format", "json");
        }
        return layout;
    }

    public String getCurrentScreenId() { return currentScreenId; }
    public String getCurrentDeviceType() { return currentDeviceType; }
}
