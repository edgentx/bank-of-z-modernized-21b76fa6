package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S3Steps {

    private CustomerAggregate aggregate;
    private UpdateCustomerDetailsCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        // Initialize a basic enrolled customer.
        // We assume the aggregate is already enrolled as per S-1 logic (simulated here for isolation).
        aggregate = new CustomerAggregate("cust-123");
        // Manually setting state to simulate an already enrolled aggregate for the context of S-3
        // In a real full-stack test, we might issue an EnrollCustomerCmd first.
        aggregate.execute(new com.example.domain.customer.model.EnrollCustomerCmd(
                "cust-123", "Old Name", "old@example.com", "GOV-ID-123"
        ));
        // Clear uncommitted events from setup to isolate test events
        aggregate.clearEvents();
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // customerId is part of the command construction
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // emailAddress is part of the command construction
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // sortCode is part of the command construction
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        // Construct a valid command based on previous Givens
        // Using defaults if the scenario didn't specify specific invalid data yet
        if (cmd == null) {
            cmd = new UpdateCustomerDetailsCmd(
                    "cust-123",
                    "New Name",
                    "new@example.com",
                    "10-20-30",
                    "1990-01-01",
                    "GOV-ID-123", // Unique ID
                    false         // No active accounts
            );
        }

        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertTrue(resultingEvents.get(0) instanceof CustomerDetailsUpdatedEvent);

        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) resultingEvents.get(0);
        Assertions.assertEquals("cust-123", event.customerId());
        Assertions.assertEquals("New Name", event.fullName());
        Assertions.assertEquals("new@example.com", event.emailAddress());
    }

    // Scenarios for Rejections

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndId() {
        aValidCustomerAggregate();
        // Setup command with invalid email and missing ID
        cmd = new UpdateCustomerDetailsCmd(
                "cust-123",
                "Name",
                "invalid-email", // Invalid email
                "10-20-30",
                "1990-01-01",
                "", // Blank Gov ID
                false
        );
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        aValidCustomerAggregate();
        // Setup command with empty name and DOB
        cmd = new UpdateCustomerDetailsCmd(
                "cust-123",
                "", // Empty Name
                "test@example.com",
                "10-20-30",
                "", // Empty DOB
                "GOV-ID-123",
                false
        );
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aValidCustomerAggregate();
        // Setup command indicating active accounts (triggering the rejection invariant)
        cmd = new UpdateCustomerDetailsCmd(
                "cust-123",
                "Name",
                "test@example.com",
                "10-20-30",
                "1990-01-01",
                "GOV-ID-123",
                true // Has active accounts - should trigger rejection
        );
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // Depending on implementation, it might be IllegalStateException or IllegalArgumentException.
        // The requirements mention "domain error", so any RuntimeException is valid.
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        System.out.println("Expected rejection caught: " + thrownException.getMessage());
    }
}
