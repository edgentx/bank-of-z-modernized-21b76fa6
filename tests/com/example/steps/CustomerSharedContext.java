package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.mocks.InMemoryCustomerRepository;

/**
 * Scenario-scoped shared state for Customer-aggregate Cucumber steps.
 * Injected via cucumber-picocontainer so multiple step classes can read/write
 * the same aggregate and exception state within a single scenario.
 */
public class CustomerSharedContext {
    public final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
    public CustomerAggregate aggregate;
    public Throwable thrownException;
}
