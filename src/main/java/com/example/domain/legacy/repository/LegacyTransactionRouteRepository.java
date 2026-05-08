package com.example.domain.legacy.repository;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import java.util.Optional;

public interface LegacyTransactionRouteRepository {
    void save(LegacyTransactionRoute aggregate);
    Optional<LegacyTransactionRoute> findById(String routeId);
}
