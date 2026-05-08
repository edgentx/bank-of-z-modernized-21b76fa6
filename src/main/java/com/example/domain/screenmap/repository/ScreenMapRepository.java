package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;

public interface ScreenMapRepository {
    ScreenMapAggregate load(String id);
    void save(ScreenMapAggregate aggregate);
}
