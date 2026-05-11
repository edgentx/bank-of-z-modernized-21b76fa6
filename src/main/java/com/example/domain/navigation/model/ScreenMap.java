package com.example.domain.navigation.model;

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
 * ScreenMap aggregate handling screen rendering logic.
 * ID: S-21
 */
public class ScreenMap extends AggregateRoot {

    private final String screenMapId;
    private String screenId;
    private String deviceType;
    private boolean rendered;

    public ScreenMap(String screenMapId) {
        this.screenMapId = screenMapId;
    }

    @Override
    public String id() {
        return screenMapId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof RenderScreenCmd) {
            return handleRenderScreen((RenderScreenCmd) cmd);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleRenderScreen(RenderScreenCmd cmd) {
        // Validation: Mandatory fields
        if (cmd.screenId() == null || cmd.screenId().isBlank()) {
            throw new IllegalArgumentException("screenId is mandatory");
        }
        if (cmd.deviceType() == null || cmd.deviceType().isBlank()) {
            throw new IllegalArgumentException("deviceType is mandatory");
        }

        // Validation: Legacy BMS constraints (Field length example: screenId <= 10)
        if (cmd.screenId().length() > 10) {
            throw new IllegalArgumentException("screenId length exceeds legacy BMS constraint (max 10 chars)");
        }

        Map<String, Object> layout = new HashMap<>();
        layout.put("screenId", cmd.screenId());
        layout.put("deviceType", cmd.deviceType());
        layout.put("timestamp", Instant.now().toString());

        ScreenRenderedEvent event = new ScreenRenderedEvent(
            this.screenMapId,
            "screen.rendered",
            cmd.screenId(),
            layout,
            Instant.now()
        );

        this.screenId = cmd.screenId();
        this.deviceType = cmd.deviceType();
        this.rendered = true;

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public boolean isRendered() {
        return rendered;
    }
}