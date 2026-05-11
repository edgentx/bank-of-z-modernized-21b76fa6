package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMap;
import java.util.Optional;

public interface ScreenMapRepository {
    ScreenMap save(ScreenMap aggregate);
    Optional<ScreenMap> findById(String id);
    // Required by existing tests/handlers if they reference load methods
    default ScreenMap load(String id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("ScreenMap not found: " + id));
    }
}