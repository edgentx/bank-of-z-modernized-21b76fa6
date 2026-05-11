package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.InMemoryCustomerRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S3Steps {

    private CustomerAggregate customer;
    private Throwable caughtException;
    private List<com.example.domain.shared.DomainEvent> resultingEvents;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        // Enroll a customer first to establish a valid state
        String customerId = "cust-123";
        customer = new CustomerAggregate(customerId);
        customer.execute(new EnrollCustomerCmd(
            customerId,
            "John Doe",
            "john.doe@example.com",
            "GOV-ID-123"
        ));
        // Clear events from enrollment so we only check update events
        customer.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        // Setup a valid base customer
        aValidCustomerAggregate();
        // The violation is provided in the command payload (e.g. nulls or invalid formats)
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        aValidCustomerAggregate();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aValidCustomerAggregate();
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Customer ID is implicitly handled by the aggregate instance
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Data is provided in the When step
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Data is provided in the When step
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        // This When step handles the positive case with valid data
        try {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
                "cust-123",
                "Jane Doe",
                "jane.doe@example.com",
                "SORT-001",
                "1990-01-01",
                "GOV-ID-456"
            );
            resultingEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the UpdateCustomerDetailsCmd command is executed with invalid email and government ID")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithInvalidEmailAndGovId() {
        try {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
                "cust-123",
                "Jane Doe",
                "invalid-email", // Invalid
                null,             // Missing GovId
                "1990-01-01",
                null              // Missing GovId
            );
            resultingEvents = customer.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @When("the UpdateCustomerDetailsCmd command is executed with empty name and date of birth")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithEmptyNameAndDob() {
        try {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
                "cust-123",
                "",    // Invalid Name
                "jane.doe@example.com",
                "SORT-001",
                null,  // Missing Dob
                "GOV-ID-456"
            );
            resultingEvents = customer.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @When("the UpdateCustomerDetailsCmd command is executed while having active accounts")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithActiveAccounts() {
        try {
            // This command includes the flag for active accounts
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
                "cust-123",
                "Jane Doe",
                "jane.doe@example.com",
                "SORT-001",
                "1990-01-01",
                "GOV-ID-456",
                true // hasActiveAccounts = true
            );
            resultingEvents = customer.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultingEvents, "Events should not be null");
        Assertions.assertEquals(1, resultingEvents.size(), "Should emit exactly one event");
        Assertions.assertEquals("customer.details.updated", resultingEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Should have thrown an exception");
        // Cucumber scenarios are descriptive; checking for any exception covers rejection
    }
}
