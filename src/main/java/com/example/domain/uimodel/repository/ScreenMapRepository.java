package com.example.domain.uimodel.repository;

import com.example.domain.userinterface.model.ScreenMap;
import java.util.Optional;

/**
 * Repository interface for ScreenMap aggregate.
 * Corrected to use the concrete aggregate class.
 */
public interface ScreenMapRepository {

    ScreenMap save(ScreenMap aggregate);

    Optional<ScreenMap> findById(String id);

    void deleteById(String id);
}
