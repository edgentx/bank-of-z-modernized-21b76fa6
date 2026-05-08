package com.example.domain.userinterface.repository;

import com.example.domain.userinterface.model.ScreenMapAggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    Optional<ScreenMapAggregate> findById(String id);
}
