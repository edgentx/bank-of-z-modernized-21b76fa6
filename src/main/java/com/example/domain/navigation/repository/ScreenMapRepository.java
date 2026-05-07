package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMapAggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    Optional<ScreenMapAggregate> findById(String screenId);
    void save(ScreenMapAggregate aggregate);
}
