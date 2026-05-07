package com.example.domain.uimodel.repository;

import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.shared.Aggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}
