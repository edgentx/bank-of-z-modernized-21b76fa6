package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input against a specific ScreenMap definition.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {
    
    public ValidateScreenInputCmd {
        if (screenId == null || screenId.isBlank()) {
            throw new IllegalArgumentException("screenId cannot be null or blank");
        }
        if (inputFields == null) {
            throw new IllegalArgumentException("inputFields cannot be null");
        }
    }
}
