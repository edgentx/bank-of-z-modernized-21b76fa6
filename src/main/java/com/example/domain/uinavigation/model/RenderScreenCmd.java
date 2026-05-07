package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

public record RenderScreenCmd(
        String screenId,
        String deviceType,
        Map<String, Object> context
) implements Command {
}