package com.example.domain.uimodel.repository;

import com.example.domain.uimodel.model.ScreenMapAggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    Optional<ScreenMapAggregate> findById(String id);
    void save(ScreenMapAggregate aggregate);
}
