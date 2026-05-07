package com.vforce360.ports;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a Report ID.
 */
public class ReportIdentifier {
    private final String id;

    public ReportIdentifier(String id) {
        this.id = id;
    }

    public static ReportIdentifier of(String id) {
        return new ReportIdentifier(id);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportIdentifier that = (ReportIdentifier) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
