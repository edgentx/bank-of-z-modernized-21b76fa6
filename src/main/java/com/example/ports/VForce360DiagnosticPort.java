package com.example.ports;

/**
 * Port interface for retrieving diagnostic data from VForce360.
 * Used to correlate internal defects with external GitHub issues.
 */
public interface VForce360DiagnosticPort {
    String fetchDefectLink(String defectId);
}