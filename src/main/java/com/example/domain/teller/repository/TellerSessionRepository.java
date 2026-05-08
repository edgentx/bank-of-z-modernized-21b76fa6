package com.example.domain.tellersession.repository;

import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import java.util.Optional;

/**
 * Aliased interface to ensure package consistency. 
 * Delegates to the root domain contract.
 */
public interface TellerSessionRepository extends com.example.domain.teller.repository.TellerSessionRepository {
    // Contract inherited from parent
}
