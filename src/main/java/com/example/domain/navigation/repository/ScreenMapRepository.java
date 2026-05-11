package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMap;

import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregate.
 */
public interface ScreenMapRepository {
    void save(ScreenMap aggregate);
    Optional<ScreenMap> findById(String id);
}