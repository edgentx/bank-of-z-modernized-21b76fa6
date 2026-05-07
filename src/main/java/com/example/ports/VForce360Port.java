package com.example.ports;

/**
 * Port for interacting with the VForce360 defect reporting system.
 * This is the boundary interface for the external service integration.
 */
public interface VForce360Port {

    /**
     * Reports a defect to the VForce360 system.
     * @param request The defect details
     * @return The resulting issue URL
     */
    String reportDefect(DefectRequest request);

    record DefectRequest(String title, String description, String severity) {}
}
