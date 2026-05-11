package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input against a specific Screen Map configuration.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {
    public ValidateScreenInputCmd {
        if (screenId == null || screenId.isBlank()) {
            throw new IllegalArgumentException("screenId cannot be null");
        }
        if (inputFields == null) {
            throw new IllegalArgumentException("inputFields cannot be null");
        }
    }
}
