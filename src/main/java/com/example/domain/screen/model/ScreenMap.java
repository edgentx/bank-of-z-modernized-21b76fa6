package com.example.domain.screen.model;

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
 * Manages the state and rules for 3270 screen definitions (BMS maps).
 * responsible for validating input field lengths and mandatory field presence.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenId;
    // A simplified registry of field definitions for this screen map.
    // Key: Field Name, Value: Definition (max length, mandatory)
    private final Map<String, FieldDefinition> fieldDefinitions = new HashMap<>();

    public ScreenMap(String screenId) {
        this.screenId = screenId;
        // Default state initialization for testing purposes.
        // In a real app, this would be loaded by events or a repository constructor.
        defineField("ACCT_NO", 10, true);
        defineField("TRANS_AMT", 12, true);
        defineField("REF_CODE", 20, false);
    }

    @Override
    public String id() {
        return screenId;
    }

    public void defineField(String name, int length, boolean mandatory) {
        fieldDefinitions.put(name, new FieldDefinition(name, length, mandatory));
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof ValidateScreenInputCmd c) {
            return validateInput(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> validateInput(ValidateScreenInputCmd cmd) {
        if (!screenId.equals(cmd.screenId())) {
             throw new IllegalArgumentException("Screen ID mismatch");
        }

        // Invariant: Validate Mandatories
        for (FieldDefinition def : fieldDefinitions.values()) {
            if (def.mandatory()) {
                String value = cmd.inputFields().get(def.name());
                if (value == null || value.trim().isEmpty()) {
                    throw new IllegalStateException(
                        String.format("All mandatory input fields must be validated before screen submission. Missing: %s", def.name())
                    );
                }
            }
        }

        // Invariant: Validate BMS Length Constraints
        for (Map.Entry<String, String> entry : cmd.inputFields().entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue();
            
            // Check against known definitions if they exist
            if (fieldDefinitions.containsKey(fieldName)) {
                FieldDefinition def = fieldDefinitions.get(fieldName);
                // Use BMS logic: check length regardless of mandatory flag
                if (value != null && value.length() > def.maxLength()) {
                    throw new IllegalStateException(
                        String.format("Field lengths must strictly adhere to legacy BMS constraints during the transition period. Field %s max %d, got %d", 
                            fieldName, def.maxLength(), value.length())
                    );
                }
            }
        }

        // Success Path
        ScreenInputValidatedEvent event = new ScreenInputValidatedEvent(
            this.id(),
            cmd.screenId(),
            cmd.inputFields(),
            Instant.now()
        );
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Inner record for field definition metadata
    private record FieldDefinition(String name, int maxLength, boolean mandatory) {}
}
