package com.vforce360.validation.ports;

import com.vforce360.validation.core.DefectReport;

/**
 * Port interface for persistence operations related to Validation.
 * Can map to DB2 or MongoDB depending on implementation.
 */
public interface ValidationRepositoryPort {
    
    /**
     * Saves the defect report state to the database.
     */
    void save(DefectReport report);
}
