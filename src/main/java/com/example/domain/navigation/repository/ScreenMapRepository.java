package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.ScreenMap;
import java.util.Optional;

public interface ScreenMapRepository {
    Optional<ScreenMap> findById(String id);
    void save(ScreenMap aggregate);
}
