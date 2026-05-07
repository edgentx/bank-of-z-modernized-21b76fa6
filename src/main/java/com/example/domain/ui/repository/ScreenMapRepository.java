package com.example.domain.ui.repository;

import com.example.domain.ui.model.ScreenMapAggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    void save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}
