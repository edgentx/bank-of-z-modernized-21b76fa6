package com.vforce360.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing the unique ID of a Report.
 */
public class ReportIdentifier {
    private final String value;

    public ReportIdentifier(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("ID cannot be null");
        this.value = value;
    }

    public static ReportIdentifier generate() {
        return new ReportIdentifier(UUID.randomUUID().toString());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportIdentifier that = (ReportIdentifier) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
