package com.example.domain.navigation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ScreenMap extends AggregateRoot {
    private final String screenId;
    private String name;
    private Map<String, Object> layout = new HashMap<>();
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

    private List<DomainEvent> render(RenderScreenCmd c) {
        if (rendered) {
            // Depending on business rules, this could be allowed or rejected. 
            // Assuming screen is regenerated if re-rendered, or state check if immutable.
            // Given context of "Generate layout", we assume it can be generated.
        }
        
        // Invariants
        if (c.screenId() == null || c.screenId().isBlank()) {
            throw new IllegalArgumentException("screenId required");
        }
        if (c.deviceType() == null || c.deviceType().isBlank()) {
            throw new IllegalArgumentException("deviceType required");
        }

        // Constraint: Field lengths must strictly adhere to legacy BMS constraints
        // Assuming BMS fields are capped at 80 chars (Legacy 3270 map constraint)
        final int BMS_MAX_LENGTH = 80;
        if (c.screenId().length() > BMS_MAX_LENGTH) {
            throw new IllegalArgumentException("screenId exceeds legacy BMS length constraint of " + BMS_MAX_LENGTH);
        }

        // Simulate layout generation logic
        Map<String, Object> generatedLayout = new HashMap<>();
        generatedLayout.put("screenId", c.screenId());
        generatedLayout.put("device", c.deviceType());
        generatedLayout.put("format", "BMS-3270");

        ScreenRenderedEvent event = new ScreenRenderedEvent(
            c.screenId(), 
            c.deviceType(), 
            "BMS-3270", 
            generatedLayout,
            Instant.now()
        );

        this.layout = generatedLayout;
        this.rendered = true;
        
        addEvent(event);
        incrementVersion();
        
        return List.of(event);
    }
}