package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate screen input against a ScreenMap definition.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {
    public ValidateScreenInputCmd {
        if (screenId == null || screenId.isBlank()) {
            throw new IllegalArgumentException("screenId cannot be null");
        }
    }
}