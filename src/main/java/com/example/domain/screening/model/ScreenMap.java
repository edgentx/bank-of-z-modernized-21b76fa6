package com.example.domain.screening.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Aggregate Root for Screen Maps.
 * Manages the definition and validation logic for 3270 emulator screens.
 * 
 * BMS Constraints (Legacy COBOL/PL-I integration):
 * - Fields must adhere to specific maximum lengths defined in the BMS map.
 * - Mandatory fields must be present and non-empty.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenId;
    
    // BMS Field Definitions for the LOGIN_SCREEN
    // In a real implementation, these would be loaded from the BMS map metadata repository.
    private static final Map<String, Integer> FIELD_DEFINITIONS = Map.of(
        "USER_ID", 8,   // Max 8 chars
        "PASSWORD", 20  // Max 20 chars
    );
    
    private static final List<String> MANDATORY_FIELDS = List.of("USER_ID", "PASSWORD");

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
            // Ensure the command targets this aggregate instance
            if (!c.screenId().equals(this.screenId)) {
                throw new IllegalArgumentException("Screen ID mismatch");
            }
            return validateInput(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> validateInput(ValidateScreenInputCmd cmd) {
        Map<String, String> inputs = cmd.inputFields();

        // 1. Validate Mandatory Fields (Invariant: All mandatory input fields must be validated before screen submission)
        for (String field : MANDATORY_FIELDS) {
            String value = inputs.get(field);
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException(
                    String.format("Validation failed: Mandatory field '%s' is missing or empty.", field)
                );
            }
        }

        // 2. Validate BMS Field Length Constraints (Invariant: Field lengths must strictly adhere to legacy BMS constraints)
        for (Map.Entry<String, String> entry : inputs.entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue();

            // We only validate fields defined in our BMS map
            if (FIELD_DEFINITIONS.containsKey(fieldName)) {
                int maxLength = FIELD_DEFINITIONS.get(fieldName);
                // BMS length is typically byte length, assuming ASCII/UTF-8 safe chars for this context
                if (value.length() > maxLength) {
                    throw new IllegalArgumentException(
                        String.format(
                            "Validation failed: Field '%s' length %d exceeds BMS constraint of %d.",
                            fieldName, value.length(), maxLength
                        )
                    );
                }
            }
        }

        // If all validations pass, emit the event
        var event = new ScreenInputValidatedEvent(
            this.screenId,
            cmd.screenId(),
            cmd.inputFields(),
            Instant.now()
        );
        
        addEvent(event);
        incrementVersion();
        
        return List.of(event);
    }
}
