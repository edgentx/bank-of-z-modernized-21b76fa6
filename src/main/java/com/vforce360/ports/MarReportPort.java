package com.vforce360.ports;

import com.vforce360.model.ModernizationAssessmentReport;
import java.util.Optional;

/**
 * Port interface for accessing Modernization Assessment Reports.
 * FIXED: Added 'findByProjectId' to match controller usage.
 */
public interface MarReportPort {
    ModernizationAssessmentReport save(ModernizationAssessmentReport report);
    Optional<ModernizationAssessmentReport> findByProjectId(String projectId);
}