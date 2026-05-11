package com.example.domain.routing.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScreenMap aggregate (user-interface-navigation).
 * Validates user input against screen map rules before routing to backend commands.
 * Story S-22.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenId;
    private Map<String, FieldDefinition> fields = new HashMap<>();
    private boolean isInitialized = false;

    // Record for Field Definition
    public record FieldDefinition(boolean mandatory, int length) {}

    public ScreenMap(String screenId) {
        this.screenId = screenId;
    }

    @Override
    public String id() {
        return screenId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof ValidateScreenInputCmd c) {
            return validateInput(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> validateInput(ValidateScreenInputCmd cmd) {
        // Ensure the aggregate is initialized with field definitions before accepting validation commands.
        // In a real scenario, this state would be loaded from the event store.
        // For S-22 implementation, we assume the aggregate state is pre-populated or loaded.

        List<String> errors = new ArrayList<>();

        // 1. Validate Mandatory Fields
        for (Map.Entry<String, FieldDefinition> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            FieldDefinition def = entry.getValue();

            if (def.mandatory()) {
                String value = cmd.inputFields().get(fieldName);
                if (value == null || value.isBlank()) {
                    errors.add("Field " + fieldName + " is mandatory");
                }
            }
        }

        // 2. Validate Legacy BMS Length Constraints
        for (Map.Entry<String, String> entry : cmd.inputFields().entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue();

            // Skip null checks for non-mandatory fields here, but check length if provided
            if (value != null && fields.containsKey(fieldName)) {
                FieldDefinition def = fields.get(fieldName);
                if (value.length() > def.length()) {
                    errors.add("Field " + fieldName + " length " + value.length() + " exceeds BMS constraint of " + def.length());
                }
            } else if (value != null && !fields.containsKey(fieldName)) {
                 // Handling unexpected fields if necessary, or ignoring.
                 // For this story, we focus on defined fields.
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Screen validation failed: " + String.join(", ", errors));
        }

        ScreenInputValidatedEvent event = new ScreenInputValidatedEvent(screenId, cmd.inputFields(), java.time.Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    /**
     * Initializes the screen map definition. 
     * Ideally called via a Setup command or during aggregate rehydration.
     * Exposed here for test setup simplicity (S-22).
     */
    public void defineField(String name, boolean mandatory, int length) {
        fields.put(name, new FieldDefinition(mandatory, length));
        this.isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}