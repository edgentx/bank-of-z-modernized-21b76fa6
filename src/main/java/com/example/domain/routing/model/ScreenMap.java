package com.example.domain.routing.model;

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
 * Handles user-interface-navigation and input validation against legacy BMS constraints.
 */
public class ScreenMap extends AggregateRoot {

    private final String id;
    // BMS Map Name for this screen definition
    private final String mapName;
    // Maximum field lengths configured in the legacy map (fieldName -> maxLength)
    private final Map<String, Integer> fieldConstraints;
    // Fields defined as mandatory in the map
    private final List<String> mandatoryFields;

    public ScreenMap(String id, String mapName, Map<String, Integer> fieldConstraints, List<String> mandatoryFields) {
        this.id = id;
        this.mapName = mapName;
        this.fieldConstraints = fieldConstraints != null ? fieldConstraints : new HashMap<>();
        this.mandatoryFields = mandatoryFields;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof ValidateScreenInputCmd c) {
            return validateInput(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> validateInput(ValidateScreenInputCmd cmd) {
        // 1. Validation: Mandatory fields check
        if (mandatoryFields != null) {
            for (String field : mandatoryFields) {
                if (!cmd.inputFields().containsKey(field) || cmd.inputFields().get(field) == null || cmd.inputFields().get(field).isBlank()) {
                    throw new IllegalArgumentException("Validation failed: Mandatory field '" + field + "' is missing or empty.");
                }
            }
        }

        // 2. Validation: BMS Length constraints
        if (fieldConstraints != null) {
            for (Map.Entry<String, String> input : cmd.inputFields().entrySet()) {
                String fieldName = input.getKey();
                String value = input.getValue();

                if (fieldConstraints.containsKey(fieldName)) {
                    int maxLength = fieldConstraints.get(fieldName);
                    if (value != null && value.length() > maxLength) {
                        throw new IllegalArgumentException(
                            "Validation failed: Field '" + fieldName + "' length " + value.length() +
                            " exceeds BMS constraint of " + maxLength + "."
                        );
                    }
                }
            }
        }

        // Success: Emit Event
        // Note: Constructor arguments match the record definition: (aggregateId, screenId, inputFields, occurredAt)
        ScreenInputValidatedEvent event = new ScreenInputValidatedEvent(
            this.id,
            cmd.screenId(),
            cmd.inputFields(),
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Getters for testing/inspection
    public String getMapName() { return mapName; }
    public Map<String, Integer> getFieldConstraints() { return fieldConstraints; }
    public List<String> getMandatoryFields() { return mandatoryFields; }
}
