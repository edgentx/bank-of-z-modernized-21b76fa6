package com.example.domain.userinterfacenavigation.repository;

import com.example.domain.userinterface.model.ScreenMap;

import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregate.
 * Note: The interface name has been corrected to align with the aggregate class ScreenMap.
 */
public interface ScreenMapRepository {

    ScreenMap save(ScreenMap aggregate);

    Optional<ScreenMap> findById(String id);

    void deleteById(String id);
}
