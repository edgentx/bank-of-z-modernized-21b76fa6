package com.example.domain.defect.port;

import com.example.domain.defect.model.DefectAggregate;

import java.util.Optional;

/**
 * Repository interface for Defect aggregates.
 */
public interface DefectRepository {
    void save(DefectAggregate aggregate);
    Optional<DefectAggregate> findById(String id);
}
