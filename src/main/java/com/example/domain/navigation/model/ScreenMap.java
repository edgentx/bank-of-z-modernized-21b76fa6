package com.example.domain.navigation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScreenMap Aggregate
 * Handles the logic for rendering screen layouts based on device constraints and user inputs.
 * Enforces legacy BMS (Basic Mapping Support) field length constraints.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenMapId;
    private String currentLayoutId;
    private DeviceType lastDeviceType;

    // Legacy BMS Constraints
    private static final int MAX_FIELD_LENGTH = 80; // Typical 3270 screen width constraint

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
            return renderScreen(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> renderScreen(RenderScreenCmd cmd) {
        // Scenario: Successfully execute RenderScreenCmd & Invariants
        // 1. Validate mandatory input fields
        if (cmd.screenId() == null || cmd.screenId().isBlank()) {
            throw new IllegalArgumentException("ScreenId cannot be null or blank");
        }
        if (cmd.layoutId() == null || cmd.layoutId().isBlank()) {
            throw new IllegalArgumentException("LayoutId cannot be null or blank");
        }
        if (cmd.inputFields() == null) {
            throw new IllegalArgumentException("InputFields map cannot be null");
        }

        // 2. Validate Field Lengths (BMS Constraints)
        // Also checks that input fields are not empty (Mandatory validation)
        if (cmd.inputFields().isEmpty()) {
            throw new IllegalArgumentException("All mandatory input fields must be validated before screen submission: Map cannot be empty");
        }

        for (Map.Entry<String, String> entry : cmd.inputFields().entrySet()) {
            String value = entry.getValue();
            if (value != null && value.length() > MAX_FIELD_LENGTH) {
                throw new IllegalArgumentException(
                    "Field lengths must strictly adhere to legacy BMS constraints during the transition period: " +
                    "field '" + entry.getKey() + "' exceeds " + MAX_FIELD_LENGTH + " characters."
                );
            }
        }

        // Create Event
        // We widen the Map<String, String> to Map<String, Object> for the event state
        Map<String, Object> presentationState = new HashMap<>(cmd.inputFields());
        // Add metadata to the state
        presentationState.put("screenId", cmd.screenId());
        presentationState.put("layoutId", cmd.layoutId());
        
        // Note: The error log showed String vs DeviceType comparison. 
        // We ensure cmd.deviceType() is used as the Enum.
        if (cmd.deviceType() == DeviceType.TERMINAL_3270) {
             presentationState.put("renderMode", "BMS_COMPATIBLE");
        } else {
             presentationState.put("renderMode", "HTML5_RESPONSIVE");
        }

        ScreenRenderedEvent event = new ScreenRenderedEvent(
            this.id(),
            cmd.screenId(),
            cmd.deviceType(),
            cmd.layoutId(),
            presentationState,
            Instant.now()
        );

        this.currentLayoutId = cmd.layoutId();
        this.lastDeviceType = cmd.deviceType();
        
        addEvent(event);
        incrementVersion();
        
        return List.of(event);
    }

    // Getters for testing/projections
    public String getCurrentLayoutId() {
        return currentLayoutId;
    }

    public DeviceType getLastDeviceType() {
        return lastDeviceType;
    }
}
