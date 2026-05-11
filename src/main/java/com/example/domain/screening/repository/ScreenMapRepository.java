package com.example.domain.screening.repository;

import com.example.domain.screening.model.ScreenMap;
import java.util.Optional;

public interface ScreenMapRepository {
    Optional<ScreenMap> findById(String screenId);
    void save(ScreenMap screenMap);
}
