package com.example.domain.navigation.model;

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
 * Aggregate root for Screen Map navigation.
 * Handles validation of user input against legacy 3270 BMS map definitions.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenId;
    private final Map<String, FieldDefinition> fields = new HashMap<>();

    public ScreenMap(String screenId) {
        this.screenId = screenId;
    }

    @Override
    public String id() {
        return screenId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof ValidateScreenInputCmd) {
            return handleValidate((ValidateScreenInputCmd) cmd);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleValidate(ValidateScreenInputCmd cmd) {
        // Invariant Check: Screen ID match
        if (!screenId.equals(cmd.getScreenId())) {
            throw new IllegalArgumentException("Command screenId does not match aggregate screenId");
        }

        List<String> errors = new ArrayList<>();
        Map<String, String> inputData = cmd.getInputFields();

        for (Map.Entry<String, FieldDefinition> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            FieldDefinition def = entry.getValue();
            String value = inputData.get(fieldName);

            // Rule: Mandatory fields must be present and non-blank
            if (def.mandatory && (value == null || value.trim().isEmpty())) {
                errors.add("Field " + fieldName + " is mandatory");
            }

            // Rule: Field lengths must adhere to legacy BMS constraints
            if (value != null && value.length() > def.length) {
                errors.add("Field " + fieldName + " exceeds max length of " + def.length);
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalStateException(String.join(", ", errors));
        }

        ScreenInputValidatedEvent event = new ScreenInputValidatedEvent(screenId, inputData, Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Internal state helper for BMS definition
    public void defineField(String name, int length, boolean mandatory) {
        fields.put(name, new FieldDefinition(name, length, mandatory));
    }

    private record FieldDefinition(String name, int length, boolean mandatory) {}
}
