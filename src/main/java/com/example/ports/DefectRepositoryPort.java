package com.example.ports;

import com.example.domain.vforce360.model.DefectAggregate;

/**
 * Repository interface for Defect aggregates.
 */
public interface DefectRepositoryPort {
    void save(DefectAggregate aggregate);
    DefectAggregate findById(String defectId);
}
