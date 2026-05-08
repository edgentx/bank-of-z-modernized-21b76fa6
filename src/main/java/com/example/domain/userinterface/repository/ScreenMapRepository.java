package com.example.domain.userinterface.repository;

import com.example.domain.userinterface.model.ScreenMapAggregate;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    ScreenMapAggregate findById(String id);
}
