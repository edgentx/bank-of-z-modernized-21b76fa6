package com.example.ports;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import java.util.Optional;

public interface VForce360RepositoryPort {
    DefectAggregate save(DefectAggregate aggregate);
    Optional<DefectAggregate> findById(String id);
}