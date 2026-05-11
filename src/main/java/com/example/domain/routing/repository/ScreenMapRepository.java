package com.example.domain.routing.repository;

import com.example.domain.routing.model.ScreenMapAggregate;
import java.util.Optional;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String screenId);
}
