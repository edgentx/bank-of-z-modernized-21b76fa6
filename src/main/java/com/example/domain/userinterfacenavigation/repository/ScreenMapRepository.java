package com.example.domain.userinterfacenavigation.repository;

import com.example.domain.navigation.model.ScreenMap;
import java.util.Optional;

/**
 * Repository interface for the ScreenMap aggregate.
 */
public interface ScreenMapRepository {
    ScreenMap save(ScreenMap aggregate);
    Optional<ScreenMap> findById(String id);
}
