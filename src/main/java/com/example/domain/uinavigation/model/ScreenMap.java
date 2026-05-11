package com.example.domain.uinavigation.model;

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
 * Manages the validation of user input against legacy BMS constraints and mandatory field rules.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenMapId;
    private final Map<String, FieldDefinition> fields = new HashMap<>();
    private boolean active;

    public ScreenMap(String screenMapId) {
        this.screenMapId = screenMapId;
        this.active = true;
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
        if (!active) {
            throw new IllegalStateException("ScreenMap is not active: " + screenMapId);
        }

        // 1. Check if all mandatory fields are present
        for (Map.Entry<String, FieldDefinition> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            FieldDefinition def = entry.getValue();

            if (def.mandatory && !cmd.inputFields().containsKey(fieldName)) {
                throw new IllegalArgumentException(
                        "All mandatory input fields must be validated before screen submission. Missing: " + fieldName
                );
            }
        }

        // 2. Check BMS Field Length Constraints
        for (Map.Entry<String, String> entry : cmd.inputFields().entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue();

            // If the field is defined in our map, check length
            if (fields.containsKey(fieldName)) {
                FieldDefinition def = fields.get(fieldName);
                if (value != null && value.length() > def.maxLength) {
                    throw new IllegalArgumentException(
                            "Field lengths must strictly adhere to legacy BMS constraints during the transition period. " +
                                    "Field '" + fieldName + "' max length is " + def.maxLength + " but input was " + value.length()
                    );
                }
            }
        }

        // Success path
        var event = new ScreenInputValidatedEvent(screenMapId, cmd.screenId(), cmd.inputFields(), Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Internal configuration helper (used in tests to setup the aggregate state)
    public void configureField(String name, boolean mandatory, int maxLength) {
        fields.put(name, new FieldDefinition(mandatory, maxLength));
    }

    public void deactivate() {
        this.active = false;
    }

    private record FieldDefinition(boolean mandatory, int maxLength) {}
}
