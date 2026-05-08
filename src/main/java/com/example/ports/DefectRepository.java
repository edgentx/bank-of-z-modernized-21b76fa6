package com.example.ports;

import com.example.domain.defect.model.DefectAggregate;

import java.util.Optional;

public interface DefectRepository {
    void save(DefectAggregate aggregate);
    Optional<DefectAggregate> findById(String id);
}
