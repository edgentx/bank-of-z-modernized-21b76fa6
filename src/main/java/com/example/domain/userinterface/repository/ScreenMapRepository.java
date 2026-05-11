package com.example.domain.userinterface.repository;

import com.example.domain.userinterface.model.ScreenMap;
import java.util.Optional;

public interface ScreenMapRepository {
    void save(ScreenMap aggregate);
    Optional<ScreenMap> findById(String id);
}
