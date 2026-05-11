package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;

public class S3Steps {

    private CustomerAggregate aggregate;
    private Exception capturedException;
    private String lastEventId;

    // Helper to create a valid base aggregate
    private CustomerAggregate createValidAggregate() {
        CustomerAggregate agg = new CustomerAggregate("cust-123");
        agg.execute(new EnrollCustomerCmd("cust-123", "John Doe", "john@example.com", "GOV123"));
        agg.clearEvents(); // Clear enrollment events to isolate updates
        return agg;
    }

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        aggregate = createValidAggregate();
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Implicit in the command construction in the 'When' step
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Implicit in the command construction in the 'When' step
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Implicit in the command construction in the 'When' step
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        executeUpdate("cust-123", "John Updated", "john.updated@example.com", LocalDate.of(1990, 1, 1), "GOV123", false, "10-20-30");
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty());
        Assertions.assertEquals("customer.details.updated", aggregate.uncommittedEvents().get(0).type());
        Assertions.assertEquals("cust-123", aggregate.uncommittedEvents().get(0).aggregateId());
    }

    // Scenario 2: Invalid Email/GovID
    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        aggregate = createValidAggregate();
    }

    @When("the UpdateCustomerDetailsCmd command is executed with invalid data")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithInvalidData() {
        // We execute specifically with invalid email/govid in mind
        try {
            aggregate.execute(new UpdateCustomerDetailsCmd("cust-123", "Jane", "invalid-email", LocalDate.now(), "", false, "SC"));
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException.getMessage().contains("valid email required") || capturedException.getMessage().contains("governmentId required"));
    }

    // Scenario 3: Name/DOB empty
    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        aggregate = createValidAggregate();
    }

    @When("the UpdateCustomerDetailsCmd command is executed with empty name and dob")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithEmptyNameAndDob() {
        try {
            aggregate.execute(new UpdateCustomerDetailsCmd("cust-123", "", "john@example.com", null, "GOV123", false, "SC"));
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    // Scenario 4: Active accounts (using the text from the prompt)
    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aggregate = createValidAggregate();
    }

    @When("the UpdateCustomerDetailsCmd command is executed with active accounts flag")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithActiveAccountsFlag() {
        try {
            // Per the aggregate logic added: hasActiveAccounts = true triggers the rejection
            aggregate.execute(new UpdateCustomerDetailsCmd("cust-123", "John", "john@example.com", LocalDate.now(), "GOV123", true, "SC"));
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    private void executeUpdate(String id, String name, String email, LocalDate dob, String govId, boolean hasActive, String sortCode) {
        try {
            var events = aggregate.execute(new UpdateCustomerDetailsCmd(id, name, email, dob, govId, hasActive, sortCode));
            if (!events.isEmpty()) {
                lastEventId = events.get(0).aggregateId();
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
