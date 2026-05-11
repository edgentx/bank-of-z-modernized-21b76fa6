package com.example.domain.screenmap.repository;

import com.example.domain.navigation.model.ScreenMap;

public interface ScreenMapRepository {
    ScreenMap save(ScreenMap aggregate);
    ScreenMap findById(String id);
}
