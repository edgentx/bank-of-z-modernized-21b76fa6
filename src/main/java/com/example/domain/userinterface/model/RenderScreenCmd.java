package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to render a specific screen layout.
 */
public record RenderScreenCmd(
        String screenId,
        String deviceType,
        Map<String, String> fields,
        int bmsFieldLengthLimit
) implements Command {}
