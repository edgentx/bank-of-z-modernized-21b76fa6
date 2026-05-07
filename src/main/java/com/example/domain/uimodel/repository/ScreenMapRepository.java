package com.example.domain.uimodel.repository;

import com.example.domain.uimodel.model.ScreenMapAggregate;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    ScreenMapAggregate findById(String id);
}
