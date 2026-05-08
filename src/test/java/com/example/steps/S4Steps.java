package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.customer.repository.CustomerRepository;
import com.example.domain.shared.UnknownCommandException;
import com.example.steps.mocks.InMemoryCustomerRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;

public class S4Steps {

    private CustomerAggregate aggregate;
    private final CustomerRepository repo = new InMemoryCustomerRepository();
    private Exception caughtException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        // Setup: Create a valid customer via EnrollCustomerCmd to ensure valid state
        aggregate = new CustomerAggregate("cust-1");
        aggregate.execute(new EnrollCustomerCmd("cust-1", "John Doe", "john@example.com", "GOV-ID-123"));
    }

    @Given("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Implicitly handled by the aggregate initialized in previous step
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        aggregate = new CustomerAggregate("cust-bad-id");
        // Hydrating with a known bad state to simulate existing invalid data
        aggregate.hydrate("Jane Doe", "invalid-email", null, 1);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        aggregate = new CustomerAggregate("cust-bad-name");
        aggregate.hydrate(null, "test@example.com", "GOV-456", 1);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aggregate = new CustomerAggregate("cust-active-acct");
        aggregate.execute(new EnrollCustomerCmd("cust-active-acct", "Rich User", "rich@example.com", "GOV-789"));
        aggregate.hydrateActiveAccounts(5); // Set active accounts to > 0
    }

    @When("the DeleteCustomerCmd command is executed")
    public void theDeleteCustomerCmdCommandIsExecuted() {
        try {
            aggregate.execute(new DeleteCustomerCmd(aggregate.id()));
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void aCustomerDeletedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        List<com.example.domain.shared.DomainEvent> events = aggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Should have emitted events");
        Assertions.assertTrue(events.get(0) instanceof CustomerDeletedEvent, "Should be CustomerDeletedEvent");
        Assertions.assertEquals("customer.deleted", events.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected exception but command succeeded");
        // Verify it's an IAE or IllegalStateException as per domain logic
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Expected domain error, got: " + caughtException.getClass().getSimpleName()
        );
    }

    // Inner class for testing infrastructure
    static class InMemoryCustomerRepository implements CustomerRepository {
        @Override
        public Optional<CustomerAggregate> findById(String customerId) {
            return Optional.empty();
        }
        @Override
        public void save(CustomerAggregate aggregate) {
            // No-op for test
        }
    }
}
