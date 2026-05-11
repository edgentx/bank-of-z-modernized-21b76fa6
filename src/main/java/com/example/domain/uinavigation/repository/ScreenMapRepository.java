package com.example.domain.uinavigation.repository;

import com.example.domain.uinavigation.model.ScreenMapAggregate;
import java.util.Optional;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}
