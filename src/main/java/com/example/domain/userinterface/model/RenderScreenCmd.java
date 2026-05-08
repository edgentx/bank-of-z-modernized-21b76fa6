package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

import java.util.List;

public record RenderScreenCmd(
    String aggregateId,
    String screenId,
    String deviceType,
    List<FieldDefinition> fields
) implements Command {}
