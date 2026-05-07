package com.vforce360.service;

import com.vforce360.domain.ModernizationAssessmentReport;
import com.vforce360.ports.ModernizationReportPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service adapter implementing the ModernizationReportPort.
 * In a full implementation, this would connect to MongoDB.
 * For the TDD Green phase, it returns a stub to verify the Controller logic.
 */
@Service
public class ModernizationReportService implements ModernizationReportPort {

    @Override
    public ModernizationAssessmentReport getReport(String projectId) {
        // Simulating a DB fetch with hardcoded data matching the test expectations
        return new ModernizationAssessmentReport(
                projectId,
                "Legacy Mainframe Modernization Assessment",
                "This document outlines the strategic path forward for the COBOL ledger system.",
                List.of("High technical debt in IMS/TM", "CICS transactions are candidates for microservice extraction"),
                Map.of("complexity", "High", "risk", "Medium")
        );
    }
}