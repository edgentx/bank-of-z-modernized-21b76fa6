package com.example.mocks;

import com.example.ports.VForce360Port;

import java.util.UUID;

/**
 * Mock adapter for VForce360.
 * Simulates the behavior of the external diagnostic system.
 */
public class MockVForce360Port implements VForce360Port {

    @Override
    public String reportDefect(String defectId, String title, String details) {
        // Simulate a successful API call returning a GitHub/VForce360 URL
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("Defect ID cannot be null");
        }
        return "https://github.com/bank-of-z/issues/" + UUID.randomUUID();
    }
}
