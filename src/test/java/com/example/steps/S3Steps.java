package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private Throwable thrownException;
    private List<DomainEvent> resultingEvents;

    // --- Given Steps ---

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customer = new CustomerAggregate("cust-123");
        // Assume valid state (enrolled) for the success path if we didn't have the 'violates' steps
        // but we manipulate state in specific steps below.
    }

    @Given("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Implicit in the aggregate ID
    }

    @Given("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Data will be provided in the When step
    }

    @Given("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Data will be provided in the When step
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndId() {
        // We prepare a command with invalid data in the 'When' step based on this context flag
        // But here we ensure the aggregate exists.
        customer = new CustomerAggregate("cust-violate-email");
        customer.setHasActiveAccounts(false); // Ensure this constraint doesn't interfere
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        customer = new CustomerAggregate("cust-violate-name");
        customer.setHasActiveAccounts(false);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        customer = new CustomerAggregate("cust-violate-accounts");
        // Set the aggregate to have active accounts
        customer.setHasActiveAccounts(true);
    }

    // --- When Steps ---

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        // We need to determine which scenario context we are in to populate the command appropriately.
        // Since Cucumber runs linearly, we can inspect the aggregate state or use a generic approach.
        // For simplicity, we try to execute a command that would violate based on the aggregate's simulated state,
        // or a valid command if no violations are set up in the Given steps.

        String id = customer.id();
        UpdateCustomerDetailsCmd cmd;

        // Heuristic to determine which scenario we are running based on the 'Given' setup.
        if (id.equals("cust-violate-email")) {
            cmd = new UpdateCustomerDetailsCmd(id, "invalid-email", "123456", null, "John Doe");
        } else if (id.equals("cust-violate-name")) {
            cmd = new UpdateCustomerDetailsCmd(id, "john@example.com", "123456", "GOV123", ""); // Empty name
        } else if (id.equals("cust-violate-accounts")) {
            // This scenario checks the aggregate invariant (hasActiveAccounts)
            cmd = new UpdateCustomerDetailsCmd(id, "john@example.com", "123456", "GOV123", "John Doe");
        } else {
            // Default: Valid inputs
            cmd = new UpdateCustomerDetailsCmd(id, "john.doe@example.com", "998877", "GOV123", "John Doe");
        }

        try {
            resultingEvents = customer.execute(cmd);
            thrownException = null;
        } catch (Exception e) {
            thrownException = e;
            resultingEvents = null;
        }
    }

    // --- Then Steps ---

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultingEvents.get(0) instanceof CustomerDetailsUpdatedEvent, "Event should be CustomerDetailsUpdatedEvent");
        assertEquals("customer.details.updated", resultingEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "An exception should have been thrown");
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException,
                "Exception should be a domain error (IllegalArgument or IllegalState)");
    }
}
