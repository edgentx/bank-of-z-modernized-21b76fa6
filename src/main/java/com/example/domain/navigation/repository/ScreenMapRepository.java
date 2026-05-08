package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMapAggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
    // Overload load/create for convenience in tests
    default ScreenMapAggregate create(String id) {
        return save(new ScreenMapAggregate(id));
    }
}
