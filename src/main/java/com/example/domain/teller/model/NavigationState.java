package com.example.domain.teller.model;

/**
 * Value object representing the Teller Terminal navigation state (3270 screen context).
 */
public record NavigationState(String screenId, String functionKey) {
    
    public boolean isValid() {
        // Basic validation: screenId must be present.
        return screenId != null && !screenId.isBlank();
    }

    public String getContext() {
        return "Screen[" + screenId + "] Key[" + (functionKey != null ? functionKey : "NONE") + "]";
    }
}
