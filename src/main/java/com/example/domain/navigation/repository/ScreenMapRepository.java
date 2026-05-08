package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMapAggregate;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    ScreenMapAggregate findById(String id);
}
