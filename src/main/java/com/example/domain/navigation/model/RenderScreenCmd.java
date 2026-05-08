package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.util.List;
import java.util.Set;

/**
 * Command to render a specific screen layout.
 * Encapsulates screen ID, device type, and input field definitions.
 */
public record RenderScreenCmd(
        String screenId,
        DeviceType deviceType,
        List<FieldSpec> inputFields,
        Set<ValidationRule> validationRules // Assumed Set based on 'strict' constraints in Scenario 3
) implements Command {

    /**
     * Definition of an input field provided by the client/user context.
     */
    public record FieldSpec(
            String name,
            int length, // Length of the data provided or intended
            boolean isMandatory
    ) {}

    /**
     * Rule defining BMS or business constraints.
     */
    public record ValidationRule(
            String fieldName,
            int maxLength
    ) {}
}
