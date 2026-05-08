package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

import java.util.Map;

public record RenderScreenCmd(
        String screenMapId,
        String screenId,
        String deviceType,
        Map<String, String> fieldValues
) implements Command {}
