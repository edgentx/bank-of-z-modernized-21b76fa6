package com.example.domain.defect.repository;

import com.example.domain.defect.model.DefectAggregate;

public interface DefectRepository {
    DefectAggregate save(DefectAggregate aggregate);
    DefectAggregate findById(String id);
}
