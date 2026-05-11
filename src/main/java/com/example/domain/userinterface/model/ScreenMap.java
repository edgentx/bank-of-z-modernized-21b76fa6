package com.example.domain.userinterface.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ScreenMap extends AggregateRoot {
    private String mapId;

    public ScreenMap(String mapId) {
        this.mapId = mapId;
    }

    @Override
    public String id() {
        return mapId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof RenderScreenCmd c) {
            return handleRenderScreen(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleRenderScreen(RenderScreenCmd cmd) {
        // Validation: All mandatory input fields
        if (cmd.screenId() == null || cmd.screenId().isBlank()) {
            throw new IllegalArgumentException("screenId is mandatory");
        }
        if (cmd.deviceType() == null) {
            throw new IllegalArgumentException("deviceType is mandatory");
        }

        // Validation: Legacy BMS constraints
        // Assuming a max length constraint for screenId based on legacy BMS mapsets
        if (cmd.screenId().length() > 7) {
            throw new IllegalArgumentException("screenId exceeds legacy BMS field length constraint (max 7 chars)");
        }

        // Logic: Generate presentation layout adapted for device
        String layoutHash = UUID.randomUUID().toString();

        var event = new ScreenRenderedEvent(this.mapId, cmd.screenId(), cmd.deviceType(), layoutHash, Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}
