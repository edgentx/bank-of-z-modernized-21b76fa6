package com.example.domain.routing.repository;

import com.example.domain.routing.model.ScreenMapAggregate;
import java.util.Optional;

public interface ScreenMapRepository {
    Optional<ScreenMapAggregate> findById(String id);
    void save(ScreenMapAggregate aggregate);
}
