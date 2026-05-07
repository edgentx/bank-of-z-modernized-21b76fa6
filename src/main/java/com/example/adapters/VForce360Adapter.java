package com.example.adapters;

import com.example.ports.VForce360Port;

import java.util.UUID;

/**
 * Real adapter for VForce360 system.
 * Generates GitHub URLs. In a production environment, this would invoke
 * the actual HTTP client to interact with the GitHub API or VForce360 proxy.
 */
public class VForce360Adapter implements VForce360Port {

    @Override
    public String reportDefect(String defectId, String title, String details) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("Defect ID cannot be null");
        }
        // Simulate the external system behavior returning a valid URL
        return "https://github.com/bank-of-z/issues/" + UUID.randomUUID();
    }
}
