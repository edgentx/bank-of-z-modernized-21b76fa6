package com.example.domain.screen.repository;

import com.example.domain.screen.model.ScreenMapAggregate;
import java.util.Optional;
public interface ScreenMapRepository {
    void save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}