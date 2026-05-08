package com.example.domain.navigation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * ScreenMap aggregate.
 * Handles validation of 3270 terminal input against legacy BMS constraints.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenMapId;
    // Simplified definition for S-22 validation logic.
    // In a real scenario, this would be loaded from the Map definition.
    private final Map<String, FieldDefinition> fields;

    public ScreenMap(String screenMapId, Map<String, FieldDefinition> fields) {
        this.screenMapId = screenMapId;
        this.fields = fields;
    }

    @Override
    public String id() {
        return screenMapId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof ValidateScreenInputCmd c) {
            return validateInput(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> validateInput(ValidateScreenInputCmd cmd) {
        // Invariant 1: Mandatory fields must be present and non-blank
        for (Map.Entry<String, FieldDefinition> entry : fields.entrySet()) {
            FieldDefinition def = entry.getValue();
            String value = cmd.inputFields().get(entry.getKey());

            if (def.mandatory && (value == null || value.isBlank())) {
                throw new IllegalArgumentException(
                    "Validation failed: Mandatory field '" + entry.getKey() + "' is missing or empty."
                );
            }

            // Invariant 2: Field lengths must strictly adhere to legacy BMS constraints
            if (value != null && value.length() > def.length) {
                throw new IllegalArgumentException(
                    "Validation failed: Field '" + entry.getKey() + "' length " + value.length() +
                    " exceeds BMS constraint of " + def.length + "."
                );
            }
        }

        var event = new ScreenInputValidatedEvent(screenMapId, cmd.screenId(), cmd.inputFields(), Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public record FieldDefinition(int length, boolean mandatory) {}
}
