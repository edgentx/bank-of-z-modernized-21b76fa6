package com.example.domain.userinterface.model;

import java.util.List;

/**
 * Simple DTO to represent field metadata for the ScreenMap.
 * Used for validation logic in the aggregate.
 */
public record FieldDefinition(
    String name,
    int row,
    int column,
    int length
) {}
