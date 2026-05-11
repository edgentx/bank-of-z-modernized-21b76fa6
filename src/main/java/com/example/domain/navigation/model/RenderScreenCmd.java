package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

public record RenderScreenCmd(
    String screenId,
    String deviceType,
    Map<String, Object> contextData
) implements Command {}
