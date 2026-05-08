package com.example.domain.navigation.repository;

import com.example.domain.navigation.model.TellerSession;
import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}
