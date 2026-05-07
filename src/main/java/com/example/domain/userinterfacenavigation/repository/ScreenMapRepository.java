package com.example.domain.userinterfacenavigation.repository;

import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}
