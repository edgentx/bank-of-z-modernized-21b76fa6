package com.example.defect.repository;

import com.example.defect.domain.DefectAggregate;

/**
 * Repository interface for Defect Aggregates.
 */
public interface DefectRepository {
    void save(DefectAggregate aggregate);
    DefectAggregate findById(String defectId);
}
