package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String screenId);
}
