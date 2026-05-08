package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

import java.util.Map;

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
