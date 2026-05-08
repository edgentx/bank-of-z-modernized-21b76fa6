package com.example.domain.defect.repository;

import com.example.domain.defect.model.DefectAggregate;

/**
 * Repository interface for Defect aggregates.
 */
public interface DefectRepository {
    DefectAggregate save(DefectAggregate aggregate);
    DefectAggregate findById(String defectId);
}
