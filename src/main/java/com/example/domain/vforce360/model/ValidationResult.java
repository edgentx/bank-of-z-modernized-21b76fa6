package com.example.domain.vforce360.model;

/**
 * Value object representing the result of a validation check.
 */
public record ValidationResult(
        boolean isValid,
        String message
) {
    public static ValidationResult valid() {
        return new ValidationResult(true, "Validation passed");
    }

    public static ValidationResult invalid(String reason) {
        return new ValidationResult(false, reason);
    }
}
