package com.example.domain.defect.repository;

import com.example.domain.defect.model.DefectAggregate;
import java.util.Optional;

public interface DefectRepository {
    DefectAggregate save(DefectAggregate aggregate);
    Optional<DefectAggregate> findById(String id);
}