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
 * Handles the rendering of screen layouts adapted for user devices.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenMapId;
    private String screenId;
    private String deviceType;
    private Map<String, String> layoutAttributes;

    // Max field length adhering to legacy BMS constraints (e.g., 3270 buffer sizes)
    private static final int MAX_SCREEN_ID_LENGTH = 8;
    private static final int MAX_DEVICE_TYPE_LENGTH = 16;

    public ScreenMap(String screenMapId) {
        this.screenMapId = screenMapId;
        this.layoutAttributes = new HashMap<>();
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
        // 1. Validate Mandatories
        if (cmd.screenId() == null || cmd.screenId().isBlank()) {
            throw new IllegalArgumentException("screenId is mandatory");
        }
        if (cmd.deviceType() == null || cmd.deviceType().isBlank()) {
            throw new IllegalArgumentException("deviceType is mandatory");
        }

        // 2. Validate Legacy Constraints
        if (cmd.screenId().length() > MAX_SCREEN_ID_LENGTH) {
            throw new IllegalArgumentException("Field lengths must strictly adhere to legacy BMS constraints: screenId exceeds max length of " + MAX_SCREEN_ID_LENGTH);
        }
        if (cmd.deviceType().length() > MAX_DEVICE_TYPE_LENGTH) {
            throw new IllegalArgumentException("Field lengths must strictly adhere to legacy BMS constraints: deviceType exceeds max length of " + MAX_DEVICE_TYPE_LENGTH);
        }

        // 3. Apply Logic
        this.screenId = cmd.screenId();
        this.deviceType = cmd.deviceType();
        // Simulate generation of presentation layout
        this.layoutAttributes.put("format", resolveLayoutFormat(cmd.deviceType()));
        this.layoutAttributes.put("timestamp", Instant.now().toString());

        // 4. Create Event
        // Fixing the compilation error by including all required arguments
        ScreenRenderedEvent event = new ScreenRenderedEvent(
            this.screenMapId,
            this.screenId,
            this.deviceType,
            this.layoutAttributes,
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private String resolveLayoutFormat(String deviceType) {
        // Simple logic to determine layout based on device
        if (deviceType.toUpperCase().contains("MOBILE")) {
            return "RESPONSIVE_MOBILE";
        } else if (deviceType.toUpperCase().contains("3270")) {
            return "BMS_LEGACY_3270";
        }
        return "DESKTOP_WEB";
    }

    // Getters for testing/verification
    public String getScreenId() { return screenId; }
    public String getDeviceType() { return deviceType; }
    public Map<String, String> getLayoutAttributes() { return layoutAttributes; }
}
