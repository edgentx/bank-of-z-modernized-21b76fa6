package com.example.domain.userinterface.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ScreenMap extends AggregateRoot {
    private final String screenId;
    private DeviceType currentDeviceType;
    private Map<String, Object> currentLayout;
    private boolean rendered;

    public ScreenMap(String screenId) {
        this.screenId = screenId;
    }

    @Override
    public String id() {
        return screenId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof RenderScreenCmd c) {
            return render(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> render(RenderScreenCmd cmd) {
        // Validation 1: Mandatory input fields
        if (cmd.screenId() == null || cmd.screenId().isBlank()) {
            throw new IllegalArgumentException("screenId required");
        }
        if (cmd.deviceType() == null) {
            throw new IllegalArgumentException("deviceType required");
        }
        if (cmd.layoutContext() == null) {
            throw new IllegalArgumentException("layoutContext required");
        }

        // Validation 2: Legacy BMS constraints (Field lengths)
        // Assuming screenId acts as a key field which might be constrained in legacy systems
        if (cmd.screenId().length() > 8) {
            throw new IllegalArgumentException("Field 'screenId' exceeds legacy BMS length constraint of 8");
        }

        // Business Logic: Generate the presentation layout (simulated)
        this.currentDeviceType = cmd.deviceType();
        this.currentLayout = cmd.layoutContext();
        this.rendered = true;

        ScreenRenderedEvent event = new ScreenRenderedEvent(
                this.screenId,
                this.currentDeviceType,
                Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}
