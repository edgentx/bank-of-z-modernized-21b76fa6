package com.example.ports;

import com.example.domain.defect.model.DefectAggregate;
import java.util.Optional;

/**
 * Legacy Port interface for VForce360 specific interactions.
 * This file is being modified to include the DefectAggregate which was missing.
 */
public interface VForce360RepositoryPort {
    DefectAggregate saveDefect(DefectAggregate aggregate);
    Optional<DefectAggregate> findDefectById(String id);
}