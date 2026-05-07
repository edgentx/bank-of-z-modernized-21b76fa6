package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMapAggregate;
import java.util.Optional;

public interface ScreenMapRepository {
    void save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}
