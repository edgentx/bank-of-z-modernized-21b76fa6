package com.example.domain.navigation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ScreenMap aggregate.
 * Manages the state and rules of user-interface-navigation screens.
 * Enforces legacy 3270 BMS constraints such as mandatory fields and field lengths.
 */
public class ScreenMap extends AggregateRoot {

    private final String screenId;
    private String bmsMapName;
    private List<FieldDefinition> fields = new ArrayList<>();

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
        if (cmd.screenId() == null || !cmd.screenId().equals(this.screenId)) {
            throw new IllegalArgumentException("Screen ID mismatch");
        }

        // Invariant: All mandatory input fields must be validated before screen submission.
        for (FieldDefinition field : fields) {
            if (field.mandatory && !cmd.inputFields().containsKey(field.name)) {
                throw new IllegalStateException("All mandatory input fields must be validated before screen submission. Missing: " + field.name);
            }
        }

        // Invariant: Field lengths must strictly adhere to legacy BMS constraints during the transition period.
        for (Map.Entry<String, String> entry : cmd.inputFields().entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue();
            
            // Look up field definition (simplified for in-memory test)
            FieldDefinition def = fields.stream()
                .filter(f -> f.name.equals(fieldName))
                .findFirst()
                .orElse(null);

            if (def != null && def.length > 0) {
                if (value != null && value.length() > def.length) {
                     throw new IllegalStateException("Field lengths must strictly adhere to legacy BMS constraints during the transition period. Field " + fieldName + " exceeds max length " + def.length);
                }
            }
        }

        var event = new ScreenInputValidatedEvent(this.screenId, cmd.screenId(), cmd.inputFields(), Instant.now());
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    /**
     * Internal helper to configure the aggregate for testing purposes.
     * In a real scenario, this would be loaded via a repository event stream.
     */
    public void configureField(String name, boolean mandatory, int length) {
        this.fields.add(new FieldDefinition(name, mandatory, length));
    }

    private record FieldDefinition(String name, boolean mandatory, int length) {}
}