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
 * ScreenMap Aggregate.
 * Handles the logic of rendering screens for different device types, enforcing legacy constraints.
 */
public class ScreenMap extends AggregateRoot {

    private String screenId;
    private String currentLayoutId;
    private boolean initialized;

    // BMS Constraints (Legacy 3270 field limits)
    private static final int MAX_FIELD_LENGTH_BMS = 40;

    public ScreenMap(String screenId) {
        this.screenId = screenId;
    }

    /**
     * Helper to set up a valid aggregate state for testing.
     */
    public void initialize() {
        this.initialized = true;
        this.currentLayoutId = "default-layout";
    }

    @Override
    public String id() {
        return screenId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof RenderScreenCmd c) {
            return renderScreen(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> renderScreen(RenderScreenCmd cmd) {
        // Invariant: ScreenMap must be valid/initialized
        if (!initialized) {
            throw new IllegalStateException("ScreenMap is not initialized: " + screenId);
        }

        // Validation: Mandatory input fields
        // Scenario: "All mandatory input fields must be validated before screen submission"
        if (cmd.screenId() == null || cmd.screenId().isBlank()) {
            throw new IllegalArgumentException("screenId is mandatory");
        }
        if (cmd.layoutId() == null || cmd.layoutId().isBlank()) {
             // Assuming layoutId is mandatory based on legacy requirements, though not explicitly in Gherkin,
             // usually needed for rendering. However, strict adherence to Gherkin:
             // Gherkin mentions 'valid screenId' and 'valid deviceType'. 
             // I will validate those strictly.
        }
        if (cmd.deviceType() == null) {
            throw new IllegalArgumentException("deviceType is mandatory");
        }

        // Validation: Legacy BMS Constraints
        // Scenario: "Field lengths must strictly adhere to legacy BMS constraints during the transition period"
        if (cmd.deviceType() == DeviceType.TERMINAL_3270) {
            // Logic: For 3270, the LayoutId or Fields must be short. 
            // For this aggregate, we check the ID length as a proxy for field/screen ID compliance.
            if (cmd.layoutId() != null && cmd.layoutId().length() > MAX_FIELD_LENGTH_BMS) {
                throw new IllegalArgumentException("Field length exceeds BMS constraints (max " + MAX_FIELD_LENGTH_BMS + ")");
            }
        }

        // Generate Layout
        Map<String, Object> layout = generateLayout(cmd);

        var event = new ScreenRenderedEvent(
            this.screenId,
            cmd.screenId(),
            cmd.layoutId(),
            cmd.deviceType(),
            layout,
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private Map<String, Object> generateLayout(RenderScreenCmd cmd) {
        Map<String, Object> layout = new HashMap<>();
        layout.put("screenId", cmd.screenId());
        layout.put("type", cmd.deviceType().toString());
        layout.put("content", "Sample layout content for " + cmd.layoutId());
        return layout;
    }
}
