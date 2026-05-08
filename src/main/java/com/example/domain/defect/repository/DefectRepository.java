package com.example.domain.defect.repository;

import com.example.domain.defect.model.DefectAggregate;

/**
 * Repository interface for Defect Aggregates.
 */
public interface DefectRepository {
    void save(DefectAggregate aggregate);
    DefectAggregate findById(String id);
}
