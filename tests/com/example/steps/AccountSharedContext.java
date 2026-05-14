package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.mocks.InMemoryAccountRepository;

/**
 * Scenario-scoped shared state for Account-aggregate Cucumber steps.
 * Injected via cucumber-picocontainer so multiple step classes can read/write
 * the same aggregate and exception state within a single scenario.
 */
public class AccountSharedContext {
    public final InMemoryAccountRepository repository = new InMemoryAccountRepository();
    public AccountAggregate aggregate;
    public Throwable thrownException;
    /**
     * Business-level account number — distinct from {@link AccountAggregate#id()}.
     * Seeded by the "a valid accountNumber is provided" Cucumber step in {@link S6Steps}.
     */
    public String accountNumber;
}
