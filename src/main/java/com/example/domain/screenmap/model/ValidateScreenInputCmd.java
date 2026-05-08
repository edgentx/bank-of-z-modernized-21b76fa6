package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input for a specific screen.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {
    public ValidateScreenInputCmd {
        if (screenId == null || screenId.isBlank()) throw new IllegalArgumentException("screenId required");
        if (inputFields == null) throw new IllegalArgumentException("inputFields required");
    }
}