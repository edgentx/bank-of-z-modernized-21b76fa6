package com.example.domain.userinterface.repository;

import com.example.domain.userinterface.model.ScreenMapAggregate;

import java.util.Optional;

public interface ScreenMapRepository {
    Optional<ScreenMapAggregate> findById(String screenId);
    void save(ScreenMapAggregate aggregate);
}
