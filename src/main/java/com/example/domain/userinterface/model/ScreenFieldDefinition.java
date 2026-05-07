package com.example.domain.userinterface.model;

/**
 * Represents the definition of a field on a legacy 3270 screen map.
 * Used for validation of input length and mandatory status.
 */
public class ScreenFieldDefinition {
    private final String name;
    private final int length;
    private final boolean mandatory;

    public ScreenFieldDefinition(String name, int length, boolean mandatory) {
        this.name = name;
        this.length = length;
        this.mandatory = mandatory;
    }

    public String name() { return name; }
    public int length() { return length; }
    public boolean isMandatory() { return mandatory; }
}
