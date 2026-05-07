package com.example.domain.ui.repository;

import com.example.domain.ui.model.ScreenMapAggregate;

public interface ScreenMapRepository {
    ScreenMapAggregate save(ScreenMapAggregate aggregate);
    ScreenMapAggregate findById(String id);
}
