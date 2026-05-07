package com.example.mocks;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.repository.CustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MockCustomerRepository implements CustomerRepository {
    private final Map<String, CustomerAggregate> store = new HashMap<>();

    @Override
    public CustomerAggregate save(CustomerAggregate aggregate) {
        store.put(aggregate.id(), aggregate);
        return aggregate;
    }

    @Override
    public CustomerAggregate findById(String id) {
        return store.get(id);
    }
}
