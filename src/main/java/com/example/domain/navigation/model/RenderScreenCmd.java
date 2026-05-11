package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

public record RenderScreenCmd(String screenId, String deviceType) implements Command {
    // Using Java Record for immutable command
}
