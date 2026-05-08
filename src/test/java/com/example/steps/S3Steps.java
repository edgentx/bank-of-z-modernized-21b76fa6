package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate aggregate;
    private Exception capturedException;
    private CustomerDetailsUpdatedEvent resultingEvent;

    // Helper to initialize a valid aggregate
    private void initValidAggregate() {
        aggregate = new CustomerAggregate("cust-123");
        // Enrolling first to simulate "Valid Customer Aggregate" state that exists
        aggregate.execute(new EnrollCustomerCmd("cust-123", "John Doe", "john@example.com", "GOV-ID-123"));
        // Reset active accounts flag (default is false in our model)
        aggregate.setHasActiveAccounts(false);
    }

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        initValidAggregate();
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        initValidAggregate();
        // We simulate the violation by passing invalid data in the When step,
        // or we could modify state. The scenario implies the *Command* carries the violation.
        // But usually 'Given Aggregate violates' implies state. However, email uniqueness is external.
        // We will handle this in the When step by passing invalid email/govId in the command.
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDoB() {
        initValidAggregate();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        initValidAggregate();
        // We explicitly set the aggregate to have active accounts to satisfy the precondition for rejection.
        aggregate.setHasActiveAccounts(true);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Implicitly handled in command construction
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Implicitly handled in command construction
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Implicitly handled in command construction
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        executeCommand("new.email@example.com", "John Updated", "2020-01-01", "GOV-ID-456", false);
    }

    // Specific whens for the negative scenarios based on the Gherkin context
    @When("the UpdateCustomerDetailsCmd command is executed with invalid details")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithInvalidDetails() {
        // This links to the "violates email/govid" scenario
        // We pass invalid email (no @) or null govId to trigger the domain error.
        executeCommand("invalid-email", "John", "2020-01-01", null, false);
    }

    @When("the UpdateCustomerDetailsCmd command is executed with missing name or dob")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithMissingNameOrDob() {
        // This links to the "violates name/dob" scenario
        executeCommand("john@example.com", null, null, "GOV-ID-123", false);
    }

    @When("the UpdateCustomerDetailsCmd command is executed while having active accounts")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWhileHavingActiveAccounts() {
        // This links to the "violates active accounts" scenario
        // The aggregate is already set to hasActiveAccounts=true in the Given step.
        // We pass valid data otherwise, expecting rejection due to the aggregate state check.
        executeCommand("john@example.com", "John", "2020-01-01", "GOV-ID-123", true);
    }

    private void executeCommand(String email, String name, String dob, String govId, boolean activeAccountsFlag) {
        try {
            var cmd = new UpdateCustomerDetailsCmd(
                "cust-123",
                email,
                "10-20-30", // sortCode
                govId,
                name,
                dob,
                activeAccountsFlag // This flag is passed to the command to assist the aggregate in checking invariants
            );
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultingEvent = (CustomerDetailsUpdatedEvent) events.get(0);
            } else {
                resultingEvent = null;
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNotNull(resultingEvent, "Expected event to be emitted");
        assertEquals("customer.details.updated", resultingEvent.type());
        assertEquals("cust-123", resultingEvent.aggregateId());
        assertNull(capturedException, "Expected no exception");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain error exception");
        // Verify it's one of the expected domain exceptions (IllegalArgumentException or IllegalStateException)
        assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected IllegalArgumentException or IllegalStateException, but got: " + capturedException.getClass().getSimpleName()
        );
    }
}
