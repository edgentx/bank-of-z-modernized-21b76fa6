package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

public record RenderScreenCmd(
    String aggregateId,
    String screenId,
    String deviceType,
    String layoutMetadata,
    java.util.Map<String, Object> inputData
) implements Command {}
