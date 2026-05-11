package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

import java.util.Map;

public record RenderScreenCmd(
        String aggregateId,
        String screenId,
        String deviceType,
        Map<String, String> inputData
) implements Command {
}