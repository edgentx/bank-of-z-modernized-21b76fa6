package com.vforce360.mar.ports;

import com.vforce360.mar.domain.ModernizationAssessmentReport;
import java.util.Optional;
import java.util.UUID;

public interface MarStoragePort {
    Optional<ModernizationAssessmentReport> findById(UUID id);
}