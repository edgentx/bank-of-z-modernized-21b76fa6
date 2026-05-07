package com.example.domain.vforce.ports;

import com.example.domain.shared.Command;

/**
 * Port interface for VForce360 integration.
 * Abstracts the external PM diagnostic and defect reporting system.
 */
public interface VForce360Port {
    /**
     * Submits a defect report to VForce360.
     * @param cmd The command triggering the report.
     * @return The URL of the created GitHub issue.
     */
    String reportDefect(Command cmd);
}