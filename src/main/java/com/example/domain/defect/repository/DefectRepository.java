package com.example.domain.defect.repository;

import com.example.domain.defect.model.DefectAggregate;

import java.util.Optional;

/**
 * Repository interface for Defect Aggregates.
 * Implementation (In-memory or DB2/Mongo) is infrastructure-specific.
 */
public interface DefectRepository {

    DefectAggregate save(DefectAggregate aggregate);

    Optional<DefectAggregate> findById(String defectId);

    // E2E verification helper
    boolean existsById(String defectId);
}