package com.example.domain.uimodel.repository;

import com.example.domain.uimodel.model.ScreenMapAggregate;

public interface ScreenMapRepository {
    ScreenMapAggregate load(String id);
    void save(ScreenMapAggregate aggregate);
}
