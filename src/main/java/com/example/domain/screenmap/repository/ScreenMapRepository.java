package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import java.util.Optional;

public interface ScreenMapRepository {
    void save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}
