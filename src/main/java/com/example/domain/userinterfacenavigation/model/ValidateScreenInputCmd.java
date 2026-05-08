package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;
import java.util.Map;
import java.util.Set;

/**
 * Command to validate user input against a specific screen map definition.
 * Validations include mandatory field presence and BMS (legacy 3270) field length constraints.
 */
public record ValidateScreenInputCmd(
        String screenId,
        Map<String, String> inputFields,
        Set<String> mandatoryFields,
        Map<String, Integer> lengthConstraints // BMS map field -> max length
) implements Command {

    public ValidateScreenInputCmd {
        if (screenId == null || screenId.isBlank()) {
            throw new IllegalArgumentException("screenId cannot be null or blank");
        }
        if (inputFields == null) {
            throw new IllegalArgumentException("inputFields cannot be null");
        }
        // Optional defensive copies handled internally by aggregate if needed
    }
}
