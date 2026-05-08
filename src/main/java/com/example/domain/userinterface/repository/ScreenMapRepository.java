package com.example.domain.userinterface.repository;

import com.example.domain.userinterface.model.ScreenMap;

public interface ScreenMapRepository {
    ScreenMap save(ScreenMap aggregate);
    ScreenMap findById(String id);
}
