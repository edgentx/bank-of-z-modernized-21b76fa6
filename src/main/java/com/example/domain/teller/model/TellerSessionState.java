package com.example.domain.teller.model;

/**
 * Value Object representing the current navigation context of the teller interface.
 * Context: S-18 Navigation state.
 */
public record TellerSessionState(String screenId, String mode) {
    public TellerSessionState {
        if (screenId == null || screenId.isBlank()) throw new IllegalArgumentException("screenId required");
        if (mode == null || mode.isBlank()) throw new IllegalArgumentException("mode required");
    }
}
