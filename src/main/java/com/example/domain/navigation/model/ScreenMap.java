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
 * ScreenMap aggregate.
 * Manages the validation of user input against screen map rules (BMS constraints, mandatory fields).
 * S-22: Implement ValidateScreenInputCmd.
 */
public class ScreenMap extends AggregateRoot {

    // Using a fixed ID for the aggregate as per BDD context "a valid ScreenMap aggregate"
    public static final String AGGREGATE_ID = "SCREEN-MAP-AGGREGATE-ROOT";

    private final Map<String, FieldDefinition> fieldDefinitions = new HashMap<>();

    public ScreenMap() {
        super();
    }

    public String id() {
        return AGGREGATE_ID;
    }

    /**
     * Helper to initialize the ScreenMap with rules for testing.
     * In a real scenario, this would be loaded via events or a repository.
     */
    public void defineField(String name, boolean mandatory, int maxLength) {
        fieldDefinitions.put(name, new FieldDefinition(name, mandatory, maxLength));
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof ValidateScreenInputCmd validateCmd) {
            return validateInput(validateCmd);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> validateInput(ValidateScreenInputCmd cmd) {
        // 1. Validate Mandatory Fields
        for (FieldDefinition def : fieldDefinitions.values()) {
            if (def.mandatory) {
                String val = cmd.inputFields().get(def.name);
                if (val == null || val.isBlank()) {
                    throw new IllegalStateException(
                        "All mandatory input fields must be validated before screen submission. Missing: " + def.name
                    );
                }
            }
        }

        // 2. Validate BMS Field Length Constraints
        for (Map.Entry<String, String> entry : cmd.inputFields().entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue();
            
            FieldDefinition def = fieldDefinitions.get(fieldName);
            if (def != null && value != null && value.length() > def.maxLength) {
                throw new IllegalStateException(
                    "Field lengths must strictly adhere to legacy BMS constraints during the transition period. Violation: " + 
                    fieldName + " (max " + def.maxLength + ", got " + value.length() + ")"
                );
            }
        }

        // 3. Emit Event
        ScreenInputValidatedEvent event = new ScreenInputValidatedEvent(
            id(), 
            cmd.screenId(), 
            cmd.inputFields(), 
            Instant.now()
        );
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private record FieldDefinition(String name, boolean mandatory, int maxLength) {}
}
