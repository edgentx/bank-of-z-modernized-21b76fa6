package com.example.ports;

import com.example.domain.shared.Command;

/**
 * Port for generating defect reports (e.g. from Temporal workflows).
 * This represents the entry point for the scenario described in VW-454.
 */
public interface DefectReportGeneratorPort {
    
    /**
     * Processes a defect report command and returns a GitHub Issue URL.
     * This simulates the interaction with GitHub or the internal issue tracker.
     *
     * @param cmd The command triggering the report (e.g. _report_defect).
     * @return The URL of the created GitHub issue.
     */
    String generateDefectReportUrl(Command cmd);
}