package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final Map<String, TellerSessionAggregate> store = new HashMap<>();

    @Override
    public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public Optional<TellerSessionAggregate> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public TellerSessionAggregate create(String id) {
        // If ID not provided, generate one (though usually ID comes from agg)
        String aggId = (id != null) ? id : UUID.randomUUID().toString();
        TellerSessionAggregate aggregate = new TellerSessionAggregate(aggId);
        store.put(aggId, aggregate);
        return aggregate;
    }
}
