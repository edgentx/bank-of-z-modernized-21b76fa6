package com.example.domain.userinterface.repository;

import com.example.domain.userinterface.model.ScreenMap;
import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregate.
 * This interface defines the contract that the InMemory implementation must fulfill.
 */
public interface ScreenMapRepository {
    void save(ScreenMap aggregate);
    Optional<ScreenMap> findById(String id);
}
