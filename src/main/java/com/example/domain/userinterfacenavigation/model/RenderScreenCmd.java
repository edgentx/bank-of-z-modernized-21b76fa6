package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;
import java.util.List;

public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType,
    List<ScreenMapAggregate.FieldDefinition> fields
) implements Command {}