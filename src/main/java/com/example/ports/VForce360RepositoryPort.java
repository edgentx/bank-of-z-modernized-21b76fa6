package com.example.ports;

import com.example.domain.vforce360.model.DefectAggregate;
import java.util.Optional;

public interface VForce360RepositoryPort {
    DefectAggregate save(DefectAggregate aggregate);
    Optional<DefectAggregate> findById(String id);
}