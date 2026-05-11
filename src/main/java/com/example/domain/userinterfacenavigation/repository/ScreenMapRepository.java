package com.example.domain.userinterfacenavigation.repository;

import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    Optional<ScreenMapAggregate> findById(String id);
    void save(ScreenMapAggregate aggregate);
}
