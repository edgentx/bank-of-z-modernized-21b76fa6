package com.example.domain.navigation.model;

/**
 * Represents a single field on a 3270/BMS screen map.
 * Encapsulates legacy constraints like length and mandatory status.
 */
public record FieldDefinition(String name, int length, boolean mandatory) {
    public FieldDefinition {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Field name cannot be blank");
        if (length <= 0) throw new IllegalArgumentException("Field length must be positive");
    }
}
