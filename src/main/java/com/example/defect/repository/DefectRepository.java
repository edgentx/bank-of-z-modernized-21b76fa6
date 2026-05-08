package com.example.defect.repository;

import com.example.domain.defect.model.DefectAggregate;

import java.util.Optional;

/**
 * Repository interface for Defect Aggregates.
 * Adapters must implement this to persist defect state.
 */
public interface DefectRepository {
    void save(DefectAggregate aggregate);
    Optional<DefectAggregate> findById(String defectId);
}
