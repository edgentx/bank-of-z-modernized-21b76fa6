package com.example.domain.uinavigation.repository;

import com.example.domain.userinterface.model.ScreenMap;
import java.util.Optional;

public interface ScreenMapRepository {
    ScreenMap save(ScreenMap aggregate);
    Optional<ScreenMap> findById(String id);
}
