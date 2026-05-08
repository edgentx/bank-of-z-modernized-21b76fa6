package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {
    public ValidateScreenInputCmd {
        if (screenId == null || screenId.isBlank()) {
            throw new IllegalArgumentException("screenId cannot be null or blank");
        }
        // inputFields can be empty (e.g. function keys), but not null for safety
        if (inputFields == null) {
            throw new IllegalArgumentException("inputFields map cannot be null");
        }
    }
}
