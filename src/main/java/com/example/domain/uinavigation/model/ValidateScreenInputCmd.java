package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input against a specific Screen Map.
 * Used to enforce frontend rules before routing to backend services.
 */
public record ValidateScreenInputCmd(
        String screenId,
        Map<String, String> inputFields
) implements Command {
    // Validation of the command parameters themselves (pre-flight check)
    public ValidateScreenInputCmd {
        if (screenId == null || screenId.isBlank()) {
            throw new IllegalArgumentException("screenId cannot be null or blank");
        }
        // inputFields can be empty, but not null, for robust validation checks
        if (inputFields == null) {
            throw new IllegalArgumentException("inputFields cannot be null");
        }
    }
}
