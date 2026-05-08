package com.example.domain.screenmap.repository;

import com.example.domain.screenmap.model.ScreenMapAggregate;

public interface ScreenMapRepository {
    ScreenMapAggregate findById(String screenId);
    void save(ScreenMapAggregate aggregate);
}