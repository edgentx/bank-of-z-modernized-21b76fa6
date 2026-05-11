package com.example.domain.tellersession.repository;

import com.example.domain.shared.Aggregate; // Corrected import based on repo convention
import com.example.domain.tellersession.model.TellerSession; // Fixed: Reference actual class name
import java.util.Optional;

public interface TellerSessionRepository {
    TellerSession save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}
