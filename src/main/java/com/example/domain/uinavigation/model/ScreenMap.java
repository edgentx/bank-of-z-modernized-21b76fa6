package com.example.domain.uinavigation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ScreenMap extends AggregateRoot {

    private final String screenId;
    private Map<String, FieldDefinition> fields; // fieldName -> definition
    private Status status = Status.DRAFT;

    public enum Status {
        DRAFT, ACTIVE, ARCHIVED
    }

    public record FieldDefinition(boolean mandatory, int maxLength) {}

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
        if (!cmd.screenId().equals(this.screenId)) {
            throw new IllegalArgumentException("Screen ID mismatch");
        }

        // Business Rules / Invariants
        if (fields != null) {
            for (Map.Entry<String, FieldDefinition> entry : fields.entrySet()) {
                String fieldName = entry.getKey();
                FieldDefinition def = entry.getValue();

                String value = cmd.inputFields().get(fieldName);

                // Check 1: Mandatory fields
                if (def.mandatory() && (value == null || value.isBlank())) {
                    throw new IllegalStateException("All mandatory input fields must be validated before screen submission. Missing: " + fieldName);
                }

                // Check 2: Field Lengths
                if (value != null && value.length() > def.maxLength()) {
                    throw new IllegalStateException("Field lengths must strictly adhere to legacy BMS constraints during the transition period. Violation: " + fieldName);
                }
            }
        }

        ScreenInputValidatedEvent event = new ScreenInputValidatedEvent(
            this.screenId,
            cmd.inputFields(),
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // State helper for testing setup
    public void configureFields(Map<String, FieldDefinition> fields) {
        this.fields = fields;
    }
}